package com.example.clubcard.services;

import com.example.clubcard.domain.BlacklistToken;
import com.example.clubcard.repos.BlacklistTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlackListTokenService {
    @Autowired
    private BlacklistTokenRepo blacklistTokenRepo;

    public void addTokenToBlacklist(String token) {
        BlacklistToken blacklistToken = new BlacklistToken();
        blacklistToken.setToken(token);
        blacklistTokenRepo.save(blacklistToken);
    }
    public boolean isTokenBlacklisted(String token) {
        return blacklistTokenRepo.existsByToken(token);
    }
}
