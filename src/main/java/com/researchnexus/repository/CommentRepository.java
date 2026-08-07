package com.researchnexus.repository;

import com.researchnexus.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByDocumentIdOrderByCreatedAtAsc(Long documentId);

}