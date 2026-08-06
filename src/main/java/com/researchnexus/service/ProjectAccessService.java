package com.researchnexus.service;

import com.researchnexus.entity.*;
import com.researchnexus.repository.ProjectMemberRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectAccessService {

    private final ProjectMemberRepository repository;

    public ProjectAccessService(ProjectMemberRepository repository) {
        this.repository = repository;
    }

    // check role
    public boolean hasRole(Project project, User user, ProjectRole role) {
        Optional<ProjectMember> member =
                repository.findByProjectAndUser(project, user);

        return member.map(m -> m.getRole() == role).orElse(false);
    }

    // check membership
    public boolean isMember(Project project, User user) {
        return repository.existsByProjectAndUser(project, user);
    }

    // OWNER check (IMPORTANT)
    public boolean isOwner(Project project, User user) {
        Optional<ProjectMember> member =
                repository.findByProjectAndUser(project, user);

        return member.map(m -> m.getRole() == ProjectRole.OWNER).orElse(false);
    }

    // ADMIN OR OWNER
    public boolean isAdminOrOwner(Project project, User user) {
        Optional<ProjectMember> member =
                repository.findByProjectAndUser(project, user);

        if (member.isEmpty()) return false;

        ProjectRole role = member.get().getRole();
        return role == ProjectRole.ADMIN || role == ProjectRole.OWNER;
    }
}