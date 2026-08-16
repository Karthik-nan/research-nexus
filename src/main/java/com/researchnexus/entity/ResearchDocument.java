package com.researchnexus.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "research_documents")
public class ResearchDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String fileName;

    private String filePath;

    private String fileType;

    private LocalDateTime uploadedAt;

    // ✅ OWNER (SECURITY + AUTHORIZATION)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    // ✅ PROJECT (RBAC SCOPE)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    // ✅ DOCUMENT → COMMENTS
    @OneToMany(
            mappedBy = "document",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
}