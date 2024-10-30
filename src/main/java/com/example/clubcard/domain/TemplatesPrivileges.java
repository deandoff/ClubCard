package com.example.clubcard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "templates_privileges")
public class TemplatesPrivileges {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String template;

    @Column(nullable = false)
    private String privileges;

}
