package com.researchnexus.service;

import com.researchnexus.dto.CommentRequest;
import com.researchnexus.dto.CommentResponse;

import java.util.List;

public interface CommentService {

    CommentResponse addComment(Long documentId, CommentRequest request);

    List<CommentResponse> getComments(Long documentId);

    void deleteComment(Long commentId);

}