package com.researchnexus.service.impl;

import com.researchnexus.entity.ResearchDocument;
import com.researchnexus.repository.ResearchDocumentRepository;
import com.researchnexus.service.DocumentService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.time.LocalDateTime;

@Service
public class DocumentServiceImpl
        implements DocumentService {

    private final ResearchDocumentRepository repository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public DocumentServiceImpl(
            ResearchDocumentRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public ResearchDocument uploadDocument(
            String title,
            String description,
            MultipartFile file
    ) {

        try {

            Path folder =
                    Paths.get(uploadDir);

            if (!Files.exists(folder)) {
                Files.createDirectories(folder);
            }

            String fileName =
                    System.currentTimeMillis()
                            + "_"
                            + file.getOriginalFilename();

            Path filePath =
                    folder.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING);

            ResearchDocument document =
                    ResearchDocument.builder()
                            .title(title)
                            .description(description)
                            .fileName(fileName)
                            .filePath(filePath.toString())
                            .fileType(file.getContentType())
                            .uploadedAt(LocalDateTime.now())
                            .build();

            return repository.save(document);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Upload failed"
            );

        }

    }

}