package com.um.springbootprojstructure.repository.projection;

import com.um.springbootprojstructure.entity.AccountStatus;
import com.um.springbootprojstructure.entity.UserRole;

import java.time.Instant;
import java.util.UUID;

public interface UserAccountListView {
    UUID getId();
    String getUsername();
    String getEmail();
    UserRole getRole();
    AccountStatus getStatus();
    Instant getCreatedAt();
}
