package com.researchnexus.service;

import com.researchnexus.dto.AddMemberRequest;
import com.researchnexus.dto.ProjectResponse;

import java.util.List;

public interface ProjectService {

    ProjectResponse createProject(String name, String description);

    List<ProjectResponse> getAllProjects();
    void addMember(Long projectId, AddMemberRequest request);
}