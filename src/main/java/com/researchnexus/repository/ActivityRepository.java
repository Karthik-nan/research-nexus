package com.researchnexus.repository;

import com.researchnexus.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByProjectIdOrderByCreatedAtDesc(Long projectId);

    List<Activity> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Activity> findAllByOrderByCreatedAtDesc();
}