package com.researchnexus.repository;

import com.researchnexus.entity.Project;
import com.researchnexus.entity.ProjectMember;
import com.researchnexus.entity.ProjectRole;
import com.researchnexus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    boolean existsByProjectAndUser(Project project, User user);

    Optional<ProjectMember> findByProjectAndUser(Project project, User user);

    Optional<ProjectMember> findByProjectAndRole(Project project, ProjectRole role);
}