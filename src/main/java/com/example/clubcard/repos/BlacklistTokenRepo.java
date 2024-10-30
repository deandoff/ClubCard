package com.example.clubcard.repos;

import com.example.clubcard.domain.BlacklistToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistTokenRepo extends JpaRepository<BlacklistToken, Integer> {
    boolean existsByToken(String token);
}