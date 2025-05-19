package com.library.management.system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Borrower {
    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    public Borrower(UUID id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}



