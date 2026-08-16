package com.researchnexus.service;

import com.researchnexus.dto.DocumentResponse;
import com.researchnexus.entity.Project;
import com.researchnexus.entity.ResearchDocument;
import com.researchnexus.entity.User;
import com.researchnexus.exception.ResourceNotFoundException;
import com.researchnexus.exception.UnauthorisedException;
import com.researchnexus.repository.ProjectRepository;
import com.researchnexus.repository.ResearchDocumentRepository;
import com.researchnexus.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final ResearchDocumentRepository repository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectAccessService projectAccessService;

    private final String uploadDir =
            "C:/projects/research-nexus/uploads";

    public DocumentServiceImpl(
            ResearchDocumentRepository repository,
            UserRepository userRepository,
            ProjectRepository projectRepository,
            ProjectAccessService projectAccessService
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectAccessService = projectAccessService;
    }

    private String getEmail() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return authentication.getName();
    }

    private User getUser() {
        return userRepository
                .findByEmail(getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));
    }

    private Path resolveFilePath(String filePath) {

        Path path = Paths.get(filePath);

        if (path.isAbsolute()) {
            return path;
        }

        return Paths.get(uploadDir)
                .resolve(path.getFileName().toString());
    }

    @Override
    public DocumentResponse uploadDocument(
            String title,
            String description,
            MultipartFile file,
            Long projectId
    ) {

        User user = getUser();

        Project project =
                projectRepository
                        .findById(projectId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Project not found"
                                ));

        if (!projectAccessService.isMember(project, user)) {
            throw new UnauthorisedException(
                    "You are not a member of this project"
            );
        }

        try {

            Path uploadPath =
                    Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFileName =
                    file.getOriginalFilename();

            if (originalFileName == null ||
                    originalFileName.isBlank()) {

                throw new RuntimeException(
                        "Invalid file name"
                );
            }

            String fileName =
                    System.currentTimeMillis()
                            + "_"
                            + originalFileName;

            Path filePath =
                    uploadPath.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            ResearchDocument document =
                    ResearchDocument.builder()
                            .title(title)
                            .description(description)
                            .fileName(fileName)
                            .filePath("uploads/" + fileName)
                            .fileType(file.getContentType())
                            .uploadedAt(LocalDateTime.now())
                            .user(user)
                            .project(project)
                            .build();

            ResearchDocument savedDocument =
                    repository.save(document);

            return new DocumentResponse(
                    savedDocument.getId(),
                    savedDocument.getTitle(),
                    savedDocument.getDescription(),
                    savedDocument.getFileName(),
                    savedDocument.getFilePath(),
                    savedDocument.getFileType(),
                    savedDocument.getUploadedAt(),
                    user.getName()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Upload failed: " + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public List<DocumentResponse> getAllDocuments() {

        return repository
                .findAll()
                .stream()
                .map(document ->
                        new DocumentResponse(
                                document.getId(),
                                document.getTitle(),
                                document.getDescription(),
                                document.getFileName(),
                                document.getFilePath(),
                                document.getFileType(),
                                document.getUploadedAt(),
                                document.getUser().getName()
                        )
                )
                .collect(Collectors.toList());
    }

    @Override
    public byte[] downloadDocument(Long id) {

        ResearchDocument document =
                repository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Document not found"
                                ));

        Path filePath =
                resolveFilePath(
                        document.getFilePath()
                );

        System.out.println(
                "Database path: "
                        + document.getFilePath()
        );

        System.out.println(
                "Physical path: "
                        + filePath
        );

        System.out.println(
                "File exists: "
                        + Files.exists(filePath)
        );

        if (!Files.exists(filePath)) {

            throw new ResourceNotFoundException(
                    "Physical file not found: "
                            + filePath
            );
        }

        try {

            return Files.readAllBytes(filePath);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Download failed: "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    @Transactional
    public void deleteDocument(Long id) {

        User currentUser = getUser();

        ResearchDocument document =
                repository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Document not found"
                                ));

        Project project =
                document.getProject();

        if (project == null) {

            throw new ResourceNotFoundException(
                    "Document is not associated with a project"
            );
        }

        boolean isOwner =
                projectAccessService.isOwner(
                        project,
                        currentUser
                );

        if (!isOwner) {

            throw new UnauthorisedException(
                    "Only the project owner can delete documents"
            );
        }

        Path filePath =
                resolveFilePath(
                        document.getFilePath()
                );

        repository.delete(document);

        repository.flush();

        try {

            Files.deleteIfExists(filePath);

        } catch (Exception e) {

            System.err.println(
                    "Could not delete physical file: "
                            + filePath
            );

            System.err.println(
                    "Reason: "
                            + e.getMessage()
            );
        }
    }

    @Override
    public List<DocumentResponse> getProjectDocuments(
            Long projectId
    ) {

        projectRepository
                .findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"
                        ));

        return repository
                .findByProjectId(projectId)
                .stream()
                .map(document ->
                        new DocumentResponse(
                                document.getId(),
                                document.getTitle(),
                                document.getDescription(),
                                document.getFileName(),
                                document.getFilePath(),
                                document.getFileType(),
                                document.getUploadedAt(),
                                document.getUser().getName()
                        )
                )
                .collect(Collectors.toList());
    }
}