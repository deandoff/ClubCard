package com.example.clubcard.controller;

import com.example.clubcard.config.JwtTokenProvider;
import com.example.clubcard.domain.ClubMember;
import com.example.clubcard.dto.AuthDTO;
import com.example.clubcard.dto.LoginDTO;
import com.example.clubcard.dto.RegisterDTO;
import com.example.clubcard.repos.ClubMemberRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
@Tag(name = "Auth", description = "Методы для создания и авторизации пользователя")
public class AuthController {

    private final ClubMemberRepo clubMemberRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthController(ClubMemberRepo clubMemberRepo, BCryptPasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider,
                          AuthenticationManager authenticationManager) {
        this.clubMemberRepo = clubMemberRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    @Operation(
            operationId = "Create User",
            summary = "Регистрация нового пользователя",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Пользователь успешно создан"),
                    @ApiResponse(responseCode = "409", description = "Пользователь с данным email уже существует"),
                    @ApiResponse(responseCode = "400", description = "Ошибка при заполнении анкеты")
            }
    )

    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDTO) {
        if (registerDTO.getFirstName() == null || registerDTO.getLastName() == null ||
            registerDTO.getFirstName().length() < 2 || registerDTO.getLastName().length() < 2) {
            return new ResponseEntity<>("Incorrect name or last name",HttpStatus.BAD_REQUEST);
        }

        if (registerDTO.getPassword() == null || registerDTO.getPassword().length() < 8) {
            return new ResponseEntity<>("Incorrect password len. Should be >= 8", HttpStatus.BAD_REQUEST);
        }

        if (!registerDTO.getPassword().equals(registerDTO.getPasswordConfirm())) {
            return new ResponseEntity<>("Passwords do not match", HttpStatus.BAD_REQUEST);
        }

        if (registerDTO.getEmail() == null) {
            return new ResponseEntity<>("Email is null", HttpStatus.BAD_REQUEST);
        }

        if (clubMemberRepo.existsByEmail(registerDTO.getEmail())) {
            return new ResponseEntity<>("User already exists", HttpStatus.CONFLICT);
        }

        ClubMember clubMember = new ClubMember();
        clubMember.setEmail(registerDTO.getEmail());
        clubMember.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        clubMember.setFirstName(registerDTO.getFirstName());
        clubMember.setLastName(registerDTO.getLastName());
        clubMember.setBirthday(registerDTO.getBirthday());
        clubMember.setPhone(registerDTO.getPhone());
        clubMember.setRole("USER");
        clubMember.setLocked(false);
        clubMember.setPrivilege("basic");

        clubMemberRepo.save(clubMember);
        return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(
            operationId = "User Login",
            summary = "Авторизация существуюшего пользователя",
            responses = {
                    @ApiResponse(responseCode = "401", description = "Ошибка авторизации"),
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "418", description = "EasterEgg")
            }
    )
    public ResponseEntity<AuthDTO> login(@RequestBody LoginDTO loginDTO) {

        if (loginDTO.getEmail().equals("easter") && loginDTO.getPassword().equals("egg")) {
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        }

        if (!clubMemberRepo.existsByEmail(loginDTO.getEmail())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (loginDTO.getEmail() == null || loginDTO.getPassword() == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String accessToken = jwtTokenProvider.generateToken(authentication);
        ClubMember clubMember = clubMemberRepo.findByEmail(loginDTO.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication, clubMember);

        AuthDTO authDTO = new AuthDTO(accessToken, refreshToken);
        return new ResponseEntity<>(authDTO, HttpStatus.OK);
    }
}

