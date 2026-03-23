package com.um.springbootprojstructure.dto;

import com.um.springbootprojstructure.entity.AccountStatus;
import com.um.springbootprojstructure.entity.UserRole;

import java.time.Instant;
import java.util.UUID;

public class UserListItemResponse {
    private UUID id;
    private String username;
    private String email;
    private UserRole role;
    private AccountStatus status;
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
