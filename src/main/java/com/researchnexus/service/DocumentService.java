package com.researchnexus.service;

import com.researchnexus.dto.DocumentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocumentService {

    DocumentResponse uploadDocument(
            String title,
            String description,
            MultipartFile file,
            String userEmail
    ) throws IOException;

    List<DocumentResponse> getAllDocuments();
}