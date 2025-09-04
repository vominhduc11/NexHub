package com.devwonder.auth_service.entity;

import com.devwonder.auth_service.enums.AccountStatus;
import com.devwonder.auth_service.enums.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private AccountType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AccountStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Check if account requires approval based on type
     * Only DEALER accounts require approval process
     */
    public boolean requiresApproval() {
        return type == AccountType.DEALER;
    }

    /**
     * Check if account can login
     * Account must be APPROVED and not deleted
     */
    public boolean canLogin() {
        return status == AccountStatus.APPROVED && deletedAt == null;
    }

    /**
     * Set initial status based on account type
     * DEALER accounts start as PENDING, others as APPROVED
     */
    public void setInitialStatus() {
        if (type == AccountType.DEALER) {
            this.status = AccountStatus.PENDING;
        } else {
            this.status = AccountStatus.APPROVED;
        }
    }
}