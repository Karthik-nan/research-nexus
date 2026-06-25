package com.researchnexus.service;

import com.researchnexus.dto.AddMemberRequest;
import com.researchnexus.dto.ProjectMemberResponse;
import com.researchnexus.dto.ProjectResponse;
import com.researchnexus.entity.Project;
import com.researchnexus.entity.ProjectMember;
import com.researchnexus.entity.ProjectRole;
import com.researchnexus.entity.User;
import com.researchnexus.repository.ProjectMemberRepository;
import com.researchnexus.repository.ProjectRepository;
import com.researchnexus.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                              UserRepository userRepository,
                              ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    // =========================
    // GET CURRENT USER
    // =========================
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // =========================
    // CREATE PROJECT
    // =========================
    @Override
    public ProjectResponse createProject(String name, String description) {

        User user = getCurrentUser();

        Project project = Project.builder()
                .name(name)
                .description(description)
                .createdAt(LocalDateTime.now())
                .createdBy(user)
                .build();

        Project saved = projectRepository.save(project);

        // OWNER is automatically added here
        ProjectMember owner = ProjectMember.builder()
                .project(saved)
                .user(user)
                .role(ProjectRole.OWNER)
                .build();

        projectMemberRepository.save(owner);

        return new ProjectResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getCreatedAt(),
                user.getName()
        );
    }

    // =========================
    // GET ALL PROJECTS
    // =========================
    @Override
    public List<ProjectResponse> getAllProjects() {

        return projectRepository.findAll().stream()
                .map(p -> new ProjectResponse(
                        p.getId(),
                        p.getName(),
                        p.getDescription(),
                        p.getCreatedAt(),
                        p.getCreatedBy().getName()
                ))
                .collect(Collectors.toList());
    }

    // =========================
    // ADD MEMBER (FIXED LOGIC)
    // =========================
    @Override
    public void addMember(Long projectId, AddMemberRequest request) {

        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // STEP 1: validate current user is part of project
        ProjectMember currentMembership = projectMemberRepository
                .findByProjectAndUser(project, currentUser)
                .orElseThrow(() -> new RuntimeException("You are not a member of this project"));

        // STEP 2: ONLY OWNER can add members
        if (currentMembership.getRole() != ProjectRole.OWNER) {
            throw new RuntimeException("Only OWNER can add members");
        }

        // STEP 3: prevent multiple OWNERS
        if (request.getRole() == ProjectRole.OWNER) {

            boolean ownerExists = projectMemberRepository
                    .findByProjectAndRole(project, ProjectRole.OWNER)
                    .isPresent();

            if (ownerExists) {
                throw new RuntimeException("Project already has an OWNER");
            }
        }

        // STEP 4: find target user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // STEP 5: prevent duplicate membership
        if (projectMemberRepository.existsByProjectAndUser(project, user)) {
            throw new RuntimeException("User already added to project");
        }

        // STEP 6: save member
        ProjectMember member = ProjectMember.builder()
                .project(project)
                .user(user)
                .role(request.getRole())
                .build();

        projectMemberRepository.save(member);
    }
    @Override
    public List<ProjectMemberResponse> getMembers(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User currentUser = getCurrentUser();

        boolean isMember = projectMemberRepository
                .existsByProjectAndUser(project, currentUser);

        if (!isMember) {
            throw new RuntimeException("Access denied");
        }

        return projectMemberRepository
                .findByProjectId(projectId)
                .stream()
                .map(member -> new ProjectMemberResponse(
                        member.getUser().getId(),
                        member.getUser().getEmail(),
                        member.getRole()
                ))
                .toList();
    }

    @Override
    public void removeMember(Long projectId, Long userId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new RuntimeException("Project not found"));

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        ProjectMember target =
                projectMemberRepository
                        .findByProjectAndUser(project, targetUser)
                        .orElseThrow(() ->
                                new RuntimeException("Member not found"));

        if (target.getRole() == ProjectRole.OWNER) {
            throw new RuntimeException(
                    "OWNER cannot be removed"
            );
        }

        projectMemberRepository.delete(target);
    }
}