package com.researchnexus.dto;

import com.researchnexus.entity.ProjectMember;

public class AddMemberRequest {

    private Long userId;
    private ProjectMember.Role role;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ProjectMember.Role getRole() {
        return role;
    }

    public void setRole(ProjectMember.Role role) {
        this.role = role;
    }
}