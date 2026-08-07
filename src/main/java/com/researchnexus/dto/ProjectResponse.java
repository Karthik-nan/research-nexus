package com.researchnexus.dto;

import com.researchnexus.entity.ProjectRole;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private String createdBy;
    private ProjectRole myRole;

}