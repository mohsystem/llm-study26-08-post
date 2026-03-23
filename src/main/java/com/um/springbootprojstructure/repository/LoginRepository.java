package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.UserAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;
import java.util.UUID;

public interface LoginRepository extends Repository<UserAccount, UUID> {

    /**
     * Authentication lookup: returns entity because passwordHash is required for verification.
     * Uses named parameters, no concatenation -> injection-safe.
     */
    @PreAuthorize("permitAll()")
    @Query("""
           select u from UserAccount u
           where lower(u.username) = lower(:login)
              or lower(u.email) = lower(:login)
           """)
    Optional<UserAccount> findForLogin(@Param("login") String login);
}
