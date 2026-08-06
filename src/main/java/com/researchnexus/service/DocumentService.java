package com.researchnexus.service;

import com.researchnexus.dto.DocumentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    DocumentResponse uploadDocument(
            String title,
            String description,
            MultipartFile file,
            Long projectId
    );

    List<DocumentResponse> getAllDocuments();

    byte[] downloadDocument(Long id);

    void deleteDocument(Long id);

    List<DocumentResponse> getProjectDocuments(Long projectId);
}