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



    // My projects (projects where current user is owner/member)

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


}