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

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

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
                user.getName(),
                ProjectRole.OWNER
        );
    }

    @Override
    public List<ProjectResponse> getMyProjects(User user) {

        return projectMemberRepository.findByUser(user)
                .stream()
                .map(member -> new ProjectResponse(
                        member.getProject().getId(),
                        member.getProject().getName(),
                        member.getProject().getDescription(),
                        member.getProject().getCreatedAt(),
                        member.getProject().getCreatedBy().getName(),
                        member.getRole()
                ))
                .toList();
    }

    @Override
    public List<ProjectResponse> getExploreProjects() {

        User currentUser = getCurrentUser();

        return projectRepository.findAll()
                .stream()
                .filter(project ->
                        !projectMemberRepository.existsByProjectAndUser(project, currentUser))
                .map(project -> new ProjectResponse(
                        project.getId(),
                        project.getName(),
                        project.getDescription(),
                        project.getCreatedAt(),
                        project.getCreatedBy().getName(),
                        null
                ))
                .toList();
    }

    @Override
    public List<ProjectResponse> getAllProjects() {

        return projectRepository.findAll()
                .stream()
                .map(project -> new ProjectResponse(
                        project.getId(),
                        project.getName(),
                        project.getDescription(),
                        project.getCreatedAt(),
                        project.getCreatedBy().getName(),
                        null
                ))
                .toList();
    }

    @Override
    public void addMember(Long projectId, AddMemberRequest request) {

        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        ProjectMember currentMembership = projectMemberRepository
                .findByProjectAndUser(project, currentUser)
                .orElseThrow(() -> new RuntimeException("You are not a member of this project"));

        if (currentMembership.getRole() != ProjectRole.OWNER) {
            throw new RuntimeException("Only OWNER can add members");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (projectMemberRepository.existsByProjectAndUser(project, user)) {
            throw new RuntimeException("User already added");
        }

        ProjectMember member = ProjectMember.builder()
                .project(project)
                .user(user)
                .role(request.getRole())
                .build();

        projectMemberRepository.save(member);
    }

    @Override
    public List<ProjectMemberResponse> getMembers(Long projectId) {

        return projectMemberRepository.findByProjectId(projectId)
                .stream()
                .map(member -> new ProjectMemberResponse(
                        member.getUser().getId(),
                        member.getUser().getEmail(),
                        member.getRole()
                ))
                .toList();
    }

    @Override
    public ProjectResponse getProjectById(Long projectId) {

        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        ProjectRole myRole = projectMemberRepository
                .findByProjectAndUser(project, currentUser)
                .map(ProjectMember::getRole)
                .orElse(null);

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt(),
                project.getCreatedBy().getName(),
                myRole
        );
    }

    @Override
    public void removeMember(Long projectId, Long userId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProjectMember member = projectMemberRepository
                .findByProjectAndUser(project, user)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (member.getRole() == ProjectRole.OWNER) {
            throw new RuntimeException("OWNER cannot be removed");
        }

        projectMemberRepository.delete(member);
    }
}