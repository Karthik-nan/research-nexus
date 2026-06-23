package com.researchnexus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentResponse {

    private Long id;
    private String title;
    private String description;
    private String fileName;
    private String filePath;
    private String fileType;
    private LocalDateTime uploadedAt;
    private String userName;
}