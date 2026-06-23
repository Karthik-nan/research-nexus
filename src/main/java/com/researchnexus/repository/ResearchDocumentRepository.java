package com.researchnexus.repository;

import com.researchnexus.entity.ResearchDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResearchDocumentRepository
        extends JpaRepository<ResearchDocument, Long> {
}