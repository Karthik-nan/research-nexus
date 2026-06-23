package com.researchnexus.controller;

import com.researchnexus.dto.DocumentResponse;
import com.researchnexus.entity.ResearchDocument;
import com.researchnexus.repository.ResearchDocumentRepository;
import com.researchnexus.service.DocumentService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService service;
    private final ResearchDocumentRepository repository;

    public DocumentController(DocumentService service,
                              ResearchDocumentRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    // =========================
    // UPLOAD
    // =========================
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> upload(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam MultipartFile file
    ) {
        return ResponseEntity.ok(
                service.uploadDocument(title, description, file)
        );
    }

    // =========================
    // GET ALL
    // =========================
    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getAllDocuments() {
        return ResponseEntity.ok(service.getAllDocuments());
    }

    // =========================
    // DOWNLOAD (FIXED)
    // =========================
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {

        byte[] fileData = service.downloadDocument(id);

        ResearchDocument doc = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + doc.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(fileData);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long id) {

        service.deleteDocument(id);

        return ResponseEntity.ok("Document deleted successfully");
    }
}