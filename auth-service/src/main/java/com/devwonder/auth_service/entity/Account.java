package com.devwonder.auth_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "accounts")
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "account_roles",
        joinColumns = @JoinColumn(name = "account_id", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false),
        uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "role_id"})
    )
    private Set<Role> roles = new HashSet<>();
}