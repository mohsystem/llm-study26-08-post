package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.SessionToken;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SessionTokenRepository extends Repository<SessionToken, UUID> {

    @PreAuthorize("permitAll()") // called by login service; no direct controller usage
    SessionToken save(SessionToken token);

    /**
     * Example safe lookup by token hash (no dynamic query building).
     */
    @PreAuthorize("hasAuthority('INTERNAL_AUTH') or hasAuthority('ADMIN')")
    @Query("""
           select t from SessionToken t
           where t.tokenHash = :tokenHash
             and t.revoked = false
             and t.expiresAt > :now
           """)
    Optional<SessionToken> findActiveByTokenHash(@Param("tokenHash") String tokenHash,
                                                @Param("now") Instant now);
}
