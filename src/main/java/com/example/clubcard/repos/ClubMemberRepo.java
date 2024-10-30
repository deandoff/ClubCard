package com.example.clubcard.repos;
import com.example.clubcard.domain.ClubMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClubMemberRepo extends JpaRepository<ClubMember, Long> {
    ClubMember findByEmail(String email);
    boolean existsByEmail(String email);
}
