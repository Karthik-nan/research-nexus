    package com.researchnexus.controller;

    import com.researchnexus.dto.ProjectResponse;
    import com.researchnexus.service.ProjectService;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/api/projects")
    public class ProjectController {

        private final ProjectService service;

        public ProjectController(ProjectService service) {
            this.service = service;
        }

        @PostMapping
        public ProjectResponse createProject(
                @RequestParam String name,
                @RequestParam String description
        ) {
            return service.createProject(name, description);
        }

        @GetMapping
        public List<ProjectResponse> getAllProjects() {
            return service.getAllProjects();
        }

        @PostMapping("/{projectId}/members")
        public void addMember(
                @PathVariable Long projectId,
                @RequestBody com.researchnexus.dto.AddMemberRequest request
        ) {
            service.addMember(projectId, request);
        }
    }