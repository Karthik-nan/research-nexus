package com.researchnexus.repository;

import com.researchnexus.entity.ResearchDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResearchDocumentRepository
        extends JpaRepository<ResearchDocument, Long> {
    List<ResearchDocument> findByUserEmail(String email);
    List<ResearchDocument> findByUserId(Long userId);
    List<ResearchDocument> findByProjectId(Long projectId);


}