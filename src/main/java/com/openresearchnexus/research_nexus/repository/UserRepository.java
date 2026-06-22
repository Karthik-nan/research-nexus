package com.openresearchnexus.research_nexus.repository;

import com.openresearchnexus.research_nexus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);
    List<User> findByName(String name);
}