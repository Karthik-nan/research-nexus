package com.researchnexus.controller;

import com.researchnexus.dto.AddMemberRequest;
import com.researchnexus.dto.ProjectMemberResponse;
import com.researchnexus.dto.ProjectResponse;
import com.researchnexus.entity.Project;
import com.researchnexus.entity.User;
import com.researchnexus.repository.ProjectRepository;
import com.researchnexus.repository.UserRepository;
import com.researchnexus.service.ProjectAccessService;
import com.researchnexus.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.researchnexus.dto.UpdateProjectRequest;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectAccessService accessService;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectController(ProjectService projectService,
                             ProjectAccessService accessService,
                             ProjectRepository projectRepository,
                             UserRepository userRepository) {
        this.projectService = projectService;
        this.accessService = accessService;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    // ==========================
    // CURRENT USER
    // ==========================

    private User getCurrentUser() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ==========================
    // CREATE PROJECT
    // ==========================

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectResponse request) {

        return ResponseEntity.ok(
                projectService.createProject(
                        request.getName(),
                        request.getDescription()
                )
        );
    }

    // ==========================
    // MY PROJECTS
    // ==========================

    @GetMapping("/my")
    public ResponseEntity<List<ProjectResponse>> getMyProjects() {

        return ResponseEntity.ok(
                projectService.getMyProjects(getCurrentUser())
        );
    }

    // ==========================
    // EXPLORE PROJECTS
    // ==========================

    @GetMapping("/explore")
    public ResponseEntity<List<ProjectResponse>> exploreProjects() {

        return ResponseEntity.ok(
                projectService.getExploreProjects()
        );
    }

    // ==========================
    // PROJECT DETAILS
    // Anyone logged in can view
    // basic project information.
    // ==========================

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable Long projectId) {

        return ResponseEntity.ok(
                projectService.getProjectById(projectId)
        );
    }

    // ==========================
    // ADD MEMBER (OWNER ONLY)
    // ==========================

    @PostMapping("/{projectId}/members")
    public ResponseEntity<String> addMember(@PathVariable Long projectId,
                                            @RequestBody AddMemberRequest request) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!accessService.isOwner(project, getCurrentUser())) {
            return ResponseEntity.status(403)
                    .body("Only OWNER can add members");
        }

        projectService.addMember(projectId, request);

        return ResponseEntity.ok("Member added successfully");
    }

    // ==========================
    // GET MEMBERS
    // ==========================

    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<ProjectMemberResponse>> getMembers(@PathVariable Long projectId) {

        return ResponseEntity.ok(
                projectService.getMembers(projectId)
        );
    }

    // ==========================
    // REMOVE MEMBER (OWNER ONLY)
    // ==========================

    @DeleteMapping("/{projectId}/members/{userId}")
    public ResponseEntity<String> removeMember(@PathVariable Long projectId,
                                               @PathVariable Long userId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!accessService.isOwner(project, getCurrentUser())) {
            return ResponseEntity.status(403)
                    .body("Only OWNER can remove members");
        }

        projectService.removeMember(projectId, userId);

        return ResponseEntity.ok("Member removed successfully");
    }

    // ==========================
// DELETE PROJECT (OWNER ONLY)
// ==========================

    @DeleteMapping("/{projectId}")
    public ResponseEntity<String> deleteProject(
            @PathVariable Long projectId
    ) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new RuntimeException("Project not found")
                );

        if (!accessService.isOwner(project, getCurrentUser())) {
            return ResponseEntity.status(403)
                    .body("Only OWNER can delete the project");
        }

        projectService.deleteProject(projectId);

        return ResponseEntity.ok(
                "Project deleted successfully"
        );

    }

    // ==========================
// EDIT PROJECT (OWNER ONLY)
// ==========================
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long projectId,
            @RequestBody UpdateProjectRequest request
    ) {

        System.out.println("========== UPDATE PROJECT ==========");
        System.out.println("Project ID: " + projectId);
        System.out.println("Name: " + request.getName());
        System.out.println("Description: " + request.getDescription());

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new RuntimeException("Project not found"));

        if (!accessService.isOwner(project, getCurrentUser())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                projectService.updateProject(
                        projectId,
                        request.getName(),
                        request.getDescription()
                )
        );
    }
}