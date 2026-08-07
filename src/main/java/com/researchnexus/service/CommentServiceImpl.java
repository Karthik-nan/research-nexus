package com.researchnexus.service;

import com.researchnexus.dto.CommentRequest;
import com.researchnexus.dto.CommentResponse;
import com.researchnexus.entity.Comment;
import com.researchnexus.entity.ResearchDocument;
import com.researchnexus.entity.User;
import com.researchnexus.repository.CommentRepository;
import com.researchnexus.repository.ResearchDocumentRepository;
import com.researchnexus.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ResearchDocumentRepository documentRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository,
                              ResearchDocumentRepository documentRepository,
                              UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public CommentResponse addComment(Long documentId, CommentRequest request) {

        User user = getCurrentUser();

        ResearchDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .document(document)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        Comment saved = commentRepository.save(comment);

        return new CommentResponse(
                saved.getId(),
                saved.getContent(),
                saved.getUser().getName(),
                saved.getCreatedAt()
        );
    }

    @Override
    public List<CommentResponse> getComments(Long documentId) {

        return commentRepository.findByDocumentIdOrderByCreatedAtAsc(documentId)
                .stream()
                .map(comment -> new CommentResponse(
                        comment.getId(),
                        comment.getContent(),
                        comment.getUser().getName(),
                        comment.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public void deleteComment(Long commentId) {

        User currentUser = getCurrentUser();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can delete only your own comments");
        }

        commentRepository.delete(comment);
    }
}