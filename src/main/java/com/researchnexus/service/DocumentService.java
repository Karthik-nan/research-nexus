package com.researchnexus.service;

import com.researchnexus.entity.ResearchDocument;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

    ResearchDocument uploadDocument(
            String title,
            String description,
            MultipartFile file
    );

}