package com.researchnexus.controller;

import com.researchnexus.dto.CommentRequest;
import com.researchnexus.dto.CommentResponse;
import com.researchnexus.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/document/{documentId}")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long documentId,
            @RequestBody CommentRequest request
    ) {

        return ResponseEntity.ok(
                commentService.addComment(documentId, request)
        );
    }

    @GetMapping("/document/{documentId}")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long documentId
    ) {

        return ResponseEntity.ok(
                commentService.getComments(documentId)
        );
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long commentId
    ) {

        commentService.deleteComment(commentId);

        return ResponseEntity.ok("Comment deleted successfully");
    }
}