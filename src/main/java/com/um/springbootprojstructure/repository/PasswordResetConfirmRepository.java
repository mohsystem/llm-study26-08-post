package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.PasswordResetRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetConfirmRepository extends Repository<PasswordResetRequest, UUID> {

    /**
     * Lookup reset request by token hash, must be unused and not expired.
     * Named params only -> injection-safe.
     */
    @PreAuthorize("permitAll()")
    @Query("""
           select r from PasswordResetRequest r
           join fetch r.user u
           where r.tokenHash = :tokenHash
             and r.used = false
             and r.expiresAt > :now
           """)
    Optional<PasswordResetRequest> findValidByTokenHash(@Param("tokenHash") String tokenHash,
                                                       @Param("now") Instant now);

    /**
     * Mark token as used (prevents replay). Update limited to this field only.
     */
    @PreAuthorize("permitAll()")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update PasswordResetRequest r
              set r.used = true
            where r.id = :id
           """)
    int markUsed(@Param("id") UUID id);
}
