package com.researchnexus.controller;

import com.researchnexus.entity.ResearchDocument;
import com.researchnexus.service.DocumentService;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")

public class DocumentController {

    private final DocumentService service;

    public DocumentController(
            DocumentService service
    ) {
        this.service = service;
    }

    @PostMapping(
            value="/upload",
            consumes =
                    MediaType.MULTIPART_FORM_DATA_VALUE
    )

    public ResearchDocument upload(

            @RequestParam String title,

            @RequestParam String description,

            @RequestParam MultipartFile file

    ) {

        return service.uploadDocument(
                title,
                description,
                file
        );

    }

}