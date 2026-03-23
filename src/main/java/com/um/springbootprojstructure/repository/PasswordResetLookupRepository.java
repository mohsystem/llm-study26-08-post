package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.UserAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetLookupRepository extends Repository<UserAccount, UUID> {

    /**
     * Lookup by username OR email for reset initiation.
     * Named parameter only -> injection-safe.
     * permitAll because reset-request is anonymous.
     */
    @PreAuthorize("permitAll()")
    @Query("""
           select u from UserAccount u
           where lower(u.username) = lower(:account)
              or lower(u.email) = lower(:account)
           """)
    Optional<UserAccount> findActiveAccountForReset(@Param("account") String account);
}
