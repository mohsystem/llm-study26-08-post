package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.PasswordResetRequest;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.UUID;

public interface PasswordResetRequestRepository extends Repository<PasswordResetRequest, UUID> {

    @PreAuthorize("permitAll()") // invoked by service; controller never calls repository directly
    PasswordResetRequest save(PasswordResetRequest req);
}
