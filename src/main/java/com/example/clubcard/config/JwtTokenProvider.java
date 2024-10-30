package com.example.clubcard.config;

import com.example.clubcard.domain.BlacklistToken;
import com.example.clubcard.domain.ClubMember;
import com.example.clubcard.domain.RefreshToken;
import com.example.clubcard.repos.BlacklistTokenRepo;
import com.example.clubcard.repos.RefreshTokenRepo;
import com.example.clubcard.services.ClubMemberService;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.expiration}")
    private int JWTExpiration;

    @Value("${jwt.secret}")
    private String JWTSecret;

    private final BlacklistTokenRepo blacklistTokenRepo;
    private final RefreshTokenRepo refreshTokenRepo;
    private final ClubMemberService clubMemberService;

    public JwtTokenProvider(BlacklistTokenRepo blacklistTokenRepo, RefreshTokenRepo refreshTokenRepo, ClubMemberService clubMemberService) {
        this.blacklistTokenRepo = blacklistTokenRepo;
        this.refreshTokenRepo = refreshTokenRepo;
        this.clubMemberService = clubMemberService;
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWTExpiration);

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, JWTSecret)
                .compact();

        return token;
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(JWTSecret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(JWTSecret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            saveToBlacklist(token);
            throw new AuthenticationCredentialsNotFoundException("JWT was exprired or incorrect");
        }
    }
    public void saveToBlacklist(String token) {
        BlacklistToken blacklistToken = new BlacklistToken();
        blacklistToken.setToken(token);
        blacklistTokenRepo.save(blacklistToken);
    }

    public void saveRefreshToken(String token, ClubMember clubMember, LocalDateTime expiryDate) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setClubMember(clubMember);
        refreshToken.setExpiryDate(expiryDate);
        refreshTokenRepo.save(refreshToken);
    }

    public String generateRefreshToken(Authentication authentication, ClubMember clubMember) {
        String token = generateToken(authentication);
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(JWTExpiration / 1000);

        saveRefreshToken(token, clubMember, expiryDate);
        return token;
    }

    public long getJWTExpiration() {
        return JWTExpiration;
    }

    public Authentication getAuthentication(String token) {
        // Извлекаем имя пользователя из токена
        String username = getUsernameFromToken(token);

        // Загружаем данные пользователя
        UserDetails userDetails = clubMemberService.loadUserByUsername(username);

        // Создаем объект Authentication
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
