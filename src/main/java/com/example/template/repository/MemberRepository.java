package com.example.template.repository;

import com.example.template.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Member entity operations.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Checks if a member exists by their unique email.
     */
    boolean existsByEmail(String email);

    /**
     * Retrieves a member by their email for authentication and profile management.
     */
    Optional<Member> findByEmail(String email);
}

