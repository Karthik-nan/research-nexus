package com.researchnexus.service;

import com.researchnexus.dto.DocumentResponse;
import com.researchnexus.entity.ResearchDocument;
import com.researchnexus.entity.User;
import com.researchnexus.repository.ResearchDocumentRepository;
import com.researchnexus.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${file.upload-dir}")
    private String uploadDir;

    public DocumentServiceImpl(ResearchDocumentRepository repository,
                               UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    // GET LOGGED IN USER EMAIL
    private String getLoggedInUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null ||
                auth.getName().equals("anonymousUser")) {
            throw new RuntimeException("User not authenticated");
        }

        return auth.getName();
    }

    // UPLOAD
    @Override
    public DocumentResponse uploadDocument(String title,
                                           String description,
                                           MultipartFile file) {
        try {

            Path folder = Paths.get(uploadDir);
            if (!Files.exists(folder)) {
                Files.createDirectories(folder);
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = folder.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String email = getLoggedInUserEmail();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ResearchDocument saved = repository.save(
                    ResearchDocument.builder()
                            .title(title)
                            .description(description)
                            .fileName(fileName)
                            .filePath(filePath.toString())
                            .fileType(file.getContentType())
                            .uploadedAt(LocalDateTime.now())
                            .user(user)
                            .build()
            );

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

    // GET ALL
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
                        doc.getUser() != null ? doc.getUser().getName() : "Unknown"
                ))
                .collect(Collectors.toList());
    }

    // DOWNLOAD
    @Override
    public byte[] downloadDocument(Long id) {
        try {

            String email = getLoggedInUserEmail();

            ResearchDocument doc = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Document not found"));

            if (doc.getUser() == null) {
                throw new RuntimeException("Document has no owner");
            }

            if (!doc.getUser().getEmail().equals(email)) {
                throw new RuntimeException("Not allowed to access this file");
            }

            Path filePath = Paths.get(doc.getFilePath());

            if (!Files.exists(filePath)) {
                throw new RuntimeException("File not found");
            }

            return Files.readAllBytes(filePath);

        } catch (Exception e) {
            throw new RuntimeException("Download failed: " + e.getMessage());
        }
    }

    // DELETE
    @Override
    public void deleteDocument(Long id) {
        try {
            String email = getLoggedInUserEmail();

            ResearchDocument doc = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Document not found"));

            if (doc.getUser() == null ||
                    !doc.getUser().getEmail().equals(email)) {
                throw new RuntimeException("Not allowed to delete this file");
            }

            Files.deleteIfExists(Paths.get(doc.getFilePath()));

            repository.delete(doc);

        } catch (Exception e) {
            throw new RuntimeException("Delete failed: " + e.getMessage());
        }
    }
}