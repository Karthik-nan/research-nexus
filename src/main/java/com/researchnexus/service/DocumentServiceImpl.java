package com.researchnexus.service;

import com.researchnexus.dto.DocumentResponse;
import com.researchnexus.entity.Project;
import com.researchnexus.entity.ResearchDocument;
import com.researchnexus.entity.User;
import com.researchnexus.repository.ProjectRepository;
import com.researchnexus.repository.ResearchDocumentRepository;
import com.researchnexus.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final ResearchDocumentRepository repository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    private final ProjectAccessService projectAccessService;

    private final String uploadDir = "uploads";

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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    private User getUser() {
        return userRepository.findByEmail(getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // =========================
    // UPLOAD (PROJECT RBAC HERE)
    // =========================
    @Override
    public DocumentResponse uploadDocument(
            String title,
            String description,
            MultipartFile file,
            Long projectId
    ) {
        try {

            User user = getUser();

            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            // 🔐 RBAC CHECK
            if (!projectAccessService.isMember(project, user)) {
                throw new RuntimeException("Not a project member");
            }

            Path folder = Paths.get(uploadDir);
            if (!Files.exists(folder)) {
                Files.createDirectories(folder);
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = folder.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            ResearchDocument doc = ResearchDocument.builder()
                    .title(title)
                    .description(description)
                    .fileName(fileName)
                    .filePath(filePath.toString())
                    .fileType(file.getContentType())
                    .uploadedAt(LocalDateTime.now())
                    .user(user)
                    .project(project)
                    .build();

            ResearchDocument saved = repository.save(doc);

            return new DocumentResponse(
                    saved.getId(),
                    saved.getTitle(),
                    saved.getDescription(),
                    saved.getFileName(),
                    saved.getFilePath(),
                    saved.getFileType(),
                    saved.getUploadedAt(),
                    user.getName()
            );

        } catch (Exception e) {
            throw new RuntimeException("Upload failed: " + e.getMessage());
        }
    }

    @Override
    public List<DocumentResponse> getAllDocuments() {
        return repository.findAll().stream()
                .map(doc -> new DocumentResponse(
                        doc.getId(),
                        doc.getTitle(),
                        doc.getDescription(),
                        doc.getFileName(),
                        doc.getFilePath(),
                        doc.getFileType(),
                        doc.getUploadedAt(),
                        doc.getUser().getName()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public byte[] downloadDocument(Long id) {
        try {

            ResearchDocument doc = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Not found"));

            return Files.readAllBytes(Paths.get(doc.getFilePath()));

        } catch (Exception e) {
            throw new RuntimeException("Download failed");
        }
    }

    @Override
    public void deleteDocument(Long id) {

        ResearchDocument doc = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        try {
            Files.deleteIfExists(Paths.get(doc.getFilePath()));
            repository.delete(doc);
        } catch (Exception e) {
            throw new RuntimeException("Delete failed");
        }
    }
}