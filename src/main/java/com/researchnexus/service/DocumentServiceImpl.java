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
    private final ActivityService activityService;

    private final String uploadDir =
            "C:/projects/research-nexus/uploads";

    public DocumentServiceImpl(
            ResearchDocumentRepository repository,
            UserRepository userRepository,
            ProjectRepository projectRepository,
            ProjectAccessService projectAccessService,
            ActivityService activityService
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectAccessService = projectAccessService;
        this.activityService = activityService;
    }

    // =========================
    // GET CURRENT USER EMAIL
    // =========================

    private String getEmail() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return authentication.getName();
    }

    // =========================
    // GET CURRENT USER
    // =========================

    private User getUser() {

        return userRepository
                .findByEmail(getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));
    }

    // =========================
    // RESOLVE FILE PATH
    // =========================

    private Path resolveFilePath(String filePath) {

        Path path = Paths.get(filePath);

        if (path.isAbsolute()) {
            return path;
        }

        return Paths.get(uploadDir)
                .resolve(path.getFileName().toString());
    }

    // =========================
    // UPLOAD DOCUMENT
    // =========================

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

        // Check project membership
        if (!projectAccessService.isMember(project, user)) {

            throw new UnauthorisedException(
                    "You are not a member of this project"
            );
        }

        try {

            // Create upload directory if it doesn't exist

            Path uploadPath =
                    Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Get original filename

            String originalFileName =
                    file.getOriginalFilename();

            if (originalFileName == null ||
                    originalFileName.isBlank()) {

                throw new RuntimeException(
                        "Invalid file name"
                );
            }

            // Create unique filename

            String fileName =
                    System.currentTimeMillis()
                            + "_"
                            + originalFileName;

            Path filePath =
                    uploadPath.resolve(fileName);

            // Save physical file

            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // Create document entity

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

            // Save document

            ResearchDocument savedDocument =
                    repository.save(document);

            // Create activity

            activityService.createActivity(
                    "DOCUMENT_UPLOADED",
                    "Document '"
                            + savedDocument.getFileName()
                            + "' was uploaded",
                    user,
                    project
            );

            // Return response

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
                    "Upload failed: "
                            + e.getMessage(),
                    e
            );
        }
    }

    // =========================
    // GET ALL DOCUMENTS
    // =========================

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

    // =========================
    // DOWNLOAD DOCUMENT
    // =========================

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

    // =========================
    // DELETE DOCUMENT
    // =========================

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

        // Only OWNER can delete

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

        // Save filename before deleting

        String fileName =
                document.getFileName();

        Path filePath =
                resolveFilePath(
                        document.getFilePath()
                );

        // Delete database record

        repository.delete(document);

        repository.flush();

        // Delete physical file

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

        // Create activity

        activityService.createActivity(
                "DOCUMENT_DELETED",
                "Document '"
                        + fileName
                        + "' was deleted",
                currentUser,
                project
        );
    }

    // =========================
    // GET PROJECT DOCUMENTS
    // =========================

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