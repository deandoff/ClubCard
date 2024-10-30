package com.example.clubcard.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String passwordConfirm;
    private String phone;
    private LocalDate birthday;
}
