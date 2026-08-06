package com.researchnexus.security;

import com.researchnexus.entity.Project;
import com.researchnexus.entity.ProjectMember;
import com.researchnexus.entity.ProjectRole;
import com.researchnexus.entity.User;
import com.researchnexus.repository.ProjectMemberRepository;
import com.researchnexus.repository.ProjectRepository;
import com.researchnexus.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectSecurityService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectSecurityService(ProjectMemberRepository projectMemberRepository,
                                  ProjectRepository projectRepository,
                                  UserRepository userRepository) {
        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean isMember(Long projectId, String email) {

        User user = getUser(email);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        return projectMemberRepository.existsByProjectAndUser(project, user);
    }

    public boolean hasRole(Long projectId, String email, ProjectRole role) {

        User user = getUser(email);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Optional<ProjectMember> member =
                projectMemberRepository.findByProjectAndUser(project, user);

        return member.isPresent() &&
                member.get().getRole() == role;
    }

    public boolean canUpload(Long projectId, String email) {

        return hasRole(projectId, email, ProjectRole.OWNER)
                || hasRole(projectId, email, ProjectRole.MAINTAINER);
    }

    public boolean canDelete(Long projectId, String email) {

        return hasRole(projectId, email, ProjectRole.OWNER);
    }

    public boolean canView(Long projectId, String email) {

        return isMember(projectId, email);
    }
}