package com.researchnexus.dto;

import java.time.LocalDateTime;

public record ActivityResponse(
        Long id,
        String action,
        String description,
        LocalDateTime createdAt,
        Long userId,
        String userName,
        Long projectId,
        String projectName
) {
}