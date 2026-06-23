package com.researchnexus.service;

import com.researchnexus.dto.DocumentResponse;
import com.researchnexus.entity.ResearchDocument;
import com.researchnexus.entity.User;
import com.researchnexus.repository.ResearchDocumentRepository;
import com.researchnexus.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final ResearchDocumentRepository repository;
    private final UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public DocumentServiceImpl(
            ResearchDocumentRepository repository,
            UserRepository userRepository
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public DocumentResponse uploadDocument(
            String title,
            String description,
            MultipartFile file,
            String userEmail
    ) {

        try {

            Path folder = Paths.get(uploadDir);

            if (!Files.exists(folder)) {
                Files.createDirectories(folder);
            }

            String fileName =
                    System.currentTimeMillis() + "_" + file.getOriginalFilename();

            Path filePath = folder.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ResearchDocument document = ResearchDocument.builder()
                    .title(title)
                    .description(description)
                    .fileName(fileName)
                    .filePath(filePath.toString())
                    .fileType(file.getContentType())
                    .uploadedAt(LocalDateTime.now())
                    .user(user)
                    .build();

            ResearchDocument saved = repository.save(document);

            return new DocumentResponse(
                    saved.getId(),
                    saved.getTitle(),
                    saved.getDescription(),
                    saved.getFileName(),
                    saved.getFilePath(),
                    saved.getFileType(),
                    saved.getUploadedAt(),
                    user.getName(),
                    user.getEmail()
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
                        doc.getUser().getName(),
                        doc.getUser().getEmail()
                ))
                .toList();
    }
}