package com.researchnexus.service;

import com.researchnexus.dto.AddMemberRequest;
import com.researchnexus.dto.ProjectMemberResponse;
import com.researchnexus.dto.ProjectResponse;
import com.researchnexus.entity.User;

import java.util.List;

public interface ProjectService {

    ProjectResponse createProject(
            String name,
            String description
    );

    // My projects
    List<ProjectResponse> getMyProjects(User user);

    // Explore all public projects
    List<ProjectResponse> getExploreProjects();

    // Existing methods
    List<ProjectResponse> getAllProjects();

    void addMember(
            Long projectId,
            AddMemberRequest request
    );

    List<ProjectMemberResponse> getMembers(
            Long projectId
    );

    void removeMember(
            Long projectId,
            Long userId
    );

    ProjectResponse getProjectById(
            Long projectId
    );

    // Delete project
    void deleteProject(
            Long projectId
    );

    ProjectResponse updateProject(
            Long projectId,
            String name,
            String description
    );
}