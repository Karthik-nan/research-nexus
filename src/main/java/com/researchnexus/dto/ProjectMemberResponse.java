package com.researchnexus.dto;

import com.researchnexus.entity.ProjectRole;

public class ProjectMemberResponse {

    private Long userId;
    private String email;
    private ProjectRole role;

    public ProjectMemberResponse() {}

    public ProjectMemberResponse(
            Long userId,
            String email,
            ProjectRole role
    ) {
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ProjectRole getRole() {
        return role;
    }

    public void setRole(ProjectRole role) {
        this.role = role;
    }
}