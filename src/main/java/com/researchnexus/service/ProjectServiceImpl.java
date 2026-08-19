package com.researchnexus.service;

import com.researchnexus.dto.AddMemberRequest;
import com.researchnexus.dto.ProjectMemberResponse;
import com.researchnexus.dto.ProjectResponse;
import com.researchnexus.entity.Project;
import com.researchnexus.entity.ProjectMember;
import com.researchnexus.entity.ProjectRole;
import com.researchnexus.entity.ResearchDocument;
import com.researchnexus.entity.User;
import com.researchnexus.repository.ProjectMemberRepository;
import com.researchnexus.repository.ProjectRepository;
import com.researchnexus.repository.ResearchDocumentRepository;
import com.researchnexus.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ResearchDocumentRepository researchDocumentRepository;
    private final ActivityService activityService;

    private final String uploadDir =
            "C:/projects/research-nexus/uploads";

    public ProjectServiceImpl(
            ProjectRepository projectRepository,
            UserRepository userRepository,
            ProjectMemberRepository projectMemberRepository,
            ResearchDocumentRepository researchDocumentRepository,
            ActivityService activityService
    ) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.researchDocumentRepository = researchDocumentRepository;
        this.activityService = activityService;
    }

    // =========================
    // GET CURRENT USER
    // =========================

    private User getCurrentUser() {

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return userRepository
                .findByEmail(auth.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    // =========================
    // CREATE PROJECT
    // =========================

    @Override
    @CacheEvict(
            value="myProjects",
            allEntries = true
    )
    public ProjectResponse createProject(
            String name,
            String description
    ) {

        User user = getCurrentUser();

        Project project = Project.builder()
                .name(name)
                .description(description)
                .createdAt(LocalDateTime.now())
                .createdBy(user)
                .build();

        Project saved =
                projectRepository.save(project);

        ProjectMember owner =
                ProjectMember.builder()
                        .project(saved)
                        .user(user)
                        .role(ProjectRole.OWNER)
                        .build();

        projectMemberRepository.save(owner);

        // Activity
        activityService.createActivity(
                "PROJECT_CREATED",
                "Project '" + saved.getName() + "' was created",
                user,
                saved
        );

        return new ProjectResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getCreatedAt(),
                user.getName(),
                ProjectRole.OWNER
        );
    }

    // =========================
    // MY PROJECTS
    // =========================

    @Override
    @Cacheable(value = "myProjects",
                key="#user.id")
    public List<ProjectResponse> getMyProjects(User user) {

        return projectMemberRepository
                .findByUser(user)
                .stream()
                .map(member ->
                        new ProjectResponse(
                                member.getProject().getId(),
                                member.getProject().getName(),
                                member.getProject().getDescription(),
                                member.getProject().getCreatedAt(),
                                member.getProject().getCreatedBy().getName(),
                                member.getRole()
                        )
                )
                .toList();
    }

    // =========================
    // EXPLORE PROJECTS
    // =========================

    @Override
    public List<ProjectResponse> getExploreProjects() {

        User currentUser = getCurrentUser();

        return projectRepository
                .findAll()
                .stream()
                .filter(project ->
                        !projectMemberRepository
                                .existsByProjectAndUser(
                                        project,
                                        currentUser
                                )
                )
                .map(project ->
                        new ProjectResponse(
                                project.getId(),
                                project.getName(),
                                project.getDescription(),
                                project.getCreatedAt(),
                                project.getCreatedBy().getName(),
                                null
                        )
                )
                .toList();
    }

    // =========================
    // GET ALL PROJECTS
    // =========================

    @Override
    public List<ProjectResponse> getAllProjects() {

        return projectRepository
                .findAll()
                .stream()
                .map(project ->
                        new ProjectResponse(
                                project.getId(),
                                project.getName(),
                                project.getDescription(),
                                project.getCreatedAt(),
                                project.getCreatedBy().getName(),
                                null
                        )
                )
                .toList();
    }

    // =========================
    // ADD MEMBER
    // =========================

    @Override
    public void addMember(
            Long projectId,
            AddMemberRequest request
    ) {

        User currentUser = getCurrentUser();

        Project project =
                projectRepository
                        .findById(projectId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Project not found"
                                ));

        ProjectMember currentMembership =
                projectMemberRepository
                        .findByProjectAndUser(
                                project,
                                currentUser
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "You are not a member of this project"
                                ));

        if (currentMembership.getRole()
                != ProjectRole.OWNER) {

            throw new RuntimeException(
                    "Only OWNER can add members"
            );
        }

        User user =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                ));

        if (projectMemberRepository
                .existsByProjectAndUser(
                        project,
                        user
                )) {

            throw new RuntimeException(
                    "User already added"
            );
        }

        ProjectMember member =
                ProjectMember.builder()
                        .project(project)
                        .user(user)
                        .role(request.getRole())
                        .build();

        projectMemberRepository.save(member);

        // Activity
        activityService.createActivity(
                "MEMBER_ADDED",
                "User '" + user.getName()
                        + "' was added to the project",
                currentUser,
                project
        );
    }

    // =========================
    // GET MEMBERS
    // =========================

    @Override
    public List<ProjectMemberResponse> getMembers(
            Long projectId
    ) {

        return projectMemberRepository
                .findByProjectId(projectId)
                .stream()
                .map(member ->
                        new ProjectMemberResponse(
                                member.getUser().getId(),
                                member.getUser().getEmail(),
                                member.getRole()
                        )
                )
                .toList();
    }

    // =========================
    // GET PROJECT BY ID
    // =========================

    @Override
    public ProjectResponse getProjectById(
            Long projectId
    ) {

        User currentUser = getCurrentUser();

        Project project =
                projectRepository
                        .findById(projectId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Project not found"
                                ));

        ProjectRole myRole =
                projectMemberRepository
                        .findByProjectAndUser(
                                project,
                                currentUser
                        )
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

    // =========================
    // REMOVE MEMBER
    // =========================

    @Override
    public void removeMember(
            Long projectId,
            Long userId
    ) {

        User currentUser = getCurrentUser();

        Project project =
                projectRepository
                        .findById(projectId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Project not found"
                                ));

        ProjectMember currentMembership =
                projectMemberRepository
                        .findByProjectAndUser(
                                project,
                                currentUser
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "You are not a member of this project"
                                ));

        if (currentMembership.getRole()
                != ProjectRole.OWNER) {

            throw new RuntimeException(
                    "Only OWNER can remove members"
            );
        }

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                ));

        ProjectMember member =
                projectMemberRepository
                        .findByProjectAndUser(
                                project,
                                user
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Member not found"
                                ));

        if (member.getRole()
                == ProjectRole.OWNER) {

            throw new RuntimeException(
                    "OWNER cannot be removed"
            );
        }

        projectMemberRepository.delete(member);

        // Activity
        activityService.createActivity(
                "MEMBER_REMOVED",
                "User '" + user.getName()
                        + "' was removed from the project",
                currentUser,
                project
        );
    }

    // =========================
    // DELETE PROJECT
    // =========================

    @Override
    @Transactional
    public void deleteProject(Long projectId) {

        User currentUser = getCurrentUser();

        Project project =
                projectRepository
                        .findById(projectId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Project not found"
                                ));

        ProjectMember membership =
                projectMemberRepository
                        .findByProjectAndUser(
                                project,
                                currentUser
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "You are not a member of this project"
                                ));

        if (membership.getRole()
                != ProjectRole.OWNER) {

            throw new RuntimeException(
                    "Only OWNER can delete the project"
            );
        }

        List<ResearchDocument> documents =
                researchDocumentRepository
                        .findByProjectId(projectId);

        for (ResearchDocument document : documents) {

            if (document.getFilePath() != null) {

                Path filePath;

                Path storedPath =
                        Paths.get(document.getFilePath());

                if (storedPath.isAbsolute()) {
                    filePath = storedPath;
                } else {
                    filePath =
                            Paths.get(uploadDir)
                                    .resolve(
                                            document.getFileName()
                                    );
                }

                try {
                    Files.deleteIfExists(filePath);
                } catch (Exception e) {
                    System.err.println(
                            "Could not delete file: "
                                    + filePath
                    );
                }
            }
        }


        researchDocumentRepository.deleteAll(documents);

        projectMemberRepository.deleteAll(
                projectMemberRepository
                        .findByProjectId(projectId)
        );

        projectRepository.delete(project);
    }

    // =========================
    // UPDATE PROJECT
    // =========================

    @Override
    public ProjectResponse updateProject(
            Long projectId,
            String name,
            String description
    ) {

        User currentUser = getCurrentUser();

        Project project =
                projectRepository
                        .findById(projectId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Project not found"
                                ));

        ProjectMember membership =
                projectMemberRepository
                        .findByProjectAndUser(
                                project,
                                currentUser
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "You are not a member of this project"
                                ));

        if (membership.getRole()
                != ProjectRole.OWNER) {

            throw new RuntimeException(
                    "Only OWNER can edit the project"
            );
        }

        project.setName(name);
        project.setDescription(description);

        Project updatedProject =
                projectRepository.save(project);

        // Activity
        activityService.createActivity(
                "PROJECT_UPDATED",
                "Project '" + updatedProject.getName()
                        + "' was updated",
                currentUser,
                updatedProject
        );

        return new ProjectResponse(
                updatedProject.getId(),
                updatedProject.getName(),
                updatedProject.getDescription(),
                updatedProject.getCreatedAt(),
                updatedProject.getCreatedBy().getName(),
                membership.getRole()
        );
    }
}