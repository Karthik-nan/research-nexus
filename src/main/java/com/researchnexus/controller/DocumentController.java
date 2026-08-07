package com.researchnexus.controller;

import com.researchnexus.dto.DocumentResponse;
import com.researchnexus.entity.Project;
import com.researchnexus.entity.User;
import com.researchnexus.repository.ProjectRepository;
import com.researchnexus.repository.UserRepository;
import com.researchnexus.service.DocumentService;
import com.researchnexus.service.ProjectAccessService;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService service;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectAccessService accessService;

    public DocumentController(DocumentService service,
                              ProjectRepository projectRepository,
                              UserRepository userRepository,
                              ProjectAccessService accessService) {
        this.service = service;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.accessService = accessService;
    }

    // -------------------------
    // GET CURRENT USER
    // -------------------------
    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println();
        System.out.println("========== DEBUG ==========");
        System.out.println("LOGGED USER EMAIL = " + user.getEmail());
        System.out.println("LOGGED USER ID = " + user.getId());
        System.out.println("===========================");
        System.out.println();

        return user;
    }
    // -------------------------
    // UPLOAD (MEMBER ONLY)
    // -------------------------
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam MultipartFile file,
            @RequestParam Long projectId
    ) {

        User user = getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // 🔐 RBAC CHECK
        if (!accessService.isMember(project, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not a project member");
        }

        return ResponseEntity.ok(
                service.uploadDocument(title, description, file, projectId)
        );
    }

    // -------------------------
    // GET ALL
    // -------------------------
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(service.getAllDocuments());
    }

    // -------------------------
    // DOWNLOAD (OWNER OR MEMBER)
    // -------------------------
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {

        System.out.println("DOWNLOAD API HIT");
        User user = getCurrentUser();

        byte[] data = service.downloadDocument(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
                .body(data);
    }

    // -------------------------
    // DELETE (OWNER ONLY INSIDE SERVICE)
    // -------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {

        service.deleteDocument(id);

        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<?> getProjectDocuments(
            @PathVariable Long projectId
    ) {

        User user = getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new RuntimeException("Project not found"));

        boolean allowed = accessService.isMember(project, user);

        System.out.println("IS MEMBER = " + allowed);

        if (!allowed) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You are not a project member");
        }

        return ResponseEntity.ok(
                service.getProjectDocuments(projectId)
        );
    }
}