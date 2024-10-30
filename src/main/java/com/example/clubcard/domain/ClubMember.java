package com.example.clubcard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "club_membership")
public class ClubMember {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private LocalDate birthday;

    @Column(length = 20)
    private String phone;

    @Column
    private String privilege;

    @Column(nullable = false)
    private boolean isLocked;

    @Column(length = 20)
    private String role;

    @Column()
    private String template;

    public ClubMember() {}

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

}
