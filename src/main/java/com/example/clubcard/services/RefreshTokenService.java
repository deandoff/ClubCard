package com.example.clubcard.services;

import com.example.clubcard.domain.ClubMember;
import com.example.clubcard.domain.RefreshToken;
import com.example.clubcard.repos.RefreshTokenRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepo refreshTokenRepo;

    public RefreshTokenService(RefreshTokenRepo refreshTokenRepo) {
        this.refreshTokenRepo = refreshTokenRepo;
    }

    public RefreshToken createRefreshToken(ClubMember clubMember, String token, LocalDateTime expiryDate) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(expiryDate);
        refreshToken.setClubMember(clubMember);
        return refreshTokenRepo.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepo.findByToken(token);
    }

    public void deleteByToken(String token) {
        refreshTokenRepo.deleteByToken(token);
    }
}
