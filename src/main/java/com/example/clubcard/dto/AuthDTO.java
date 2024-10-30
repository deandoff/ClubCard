package com.example.clubcard.dto;

import lombok.Data;

@Data
public class AuthDTO {
    private String accessToken;
    private String refreshToken;

    public AuthDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
