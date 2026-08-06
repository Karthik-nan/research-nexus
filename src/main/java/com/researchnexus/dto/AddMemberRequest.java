package com.researchnexus.dto;

import com.researchnexus.entity.ProjectRole;

public class AddMemberRequest {

    private String email;

    private ProjectRole role;


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