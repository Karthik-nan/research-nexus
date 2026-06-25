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

    // -------------------------
    // CURRENT USER
    // -------------------------
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // -------------------------
    // CREATE PROJECT
    // -------------------------
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectResponse request) {
        return ResponseEntity.ok(
                projectService.createProject(request.getName(), request.getDescription())
        );
    }

    // -------------------------
    // GET ALL PROJECTS
    // -------------------------
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    // -------------------------
    // ADD MEMBER (OWNER ONLY)
    // -------------------------
    @PostMapping("/{projectId}/members")
    public ResponseEntity<String> addMember(
            @PathVariable Long projectId,
            @RequestBody AddMemberRequest request) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!accessService.isOwner(project, getCurrentUser())) {
            return ResponseEntity.status(403).body("Only OWNER can add members");
        }

        projectService.addMember(projectId, request);

        return ResponseEntity.ok("Member added successfully");
    }

    // -------------------------
    // GET PROJECT (MEMBER ONLY)
    // -------------------------
    @GetMapping("/{projectId}")
    public ResponseEntity<String> getProject(@PathVariable Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!accessService.isMember(project, getCurrentUser())) {
            return ResponseEntity.status(403).body("Access denied");
        }

        return ResponseEntity.ok("Project access granted");
    }
    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<ProjectMemberResponse>>
    getMembers(@PathVariable Long projectId) {

        return ResponseEntity.ok(
                projectService.getMembers(projectId)
        );
    }
    @DeleteMapping("/{projectId}/members/{userId}")
    public ResponseEntity<String> removeMember(
            @PathVariable Long projectId,
            @PathVariable Long userId
    ) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new RuntimeException("Project not found"));

        if (!accessService.isOwner(project, getCurrentUser())) {
            return ResponseEntity
                    .status(403)
                    .body("Only OWNER can remove members");
        }

        projectService.removeMember(projectId, userId);

        return ResponseEntity.ok(
                "Member removed successfully"
        );
    }
}