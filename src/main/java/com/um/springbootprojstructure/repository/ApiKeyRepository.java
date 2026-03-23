package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.ApiKey;
import com.um.springbootprojstructure.entity.ApiKeyStatus;
import com.um.springbootprojstructure.repository.projection.ApiKeyListView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository extends Repository<ApiKey, UUID> {

    /**
     * Create key for current principal (owner enforced by service setting owner = principal.id reference).
     */
    @PreAuthorize("isAuthenticated()")
    ApiKey save(ApiKey apiKey);

    /**
     * Data minimization: projection only + pageable.
     * Ownership enforced via principal.id.
     */
    @PreAuthorize("isAuthenticated()")
    @Query("""
           select k.id as id,
                  k.name as name,
                  k.status as status,
                  k.createdAt as createdAt,
                  k.revokedAt as revokedAt
           from ApiKey k
           where k.owner.id = :#{principal.id}
           """)
    Page<ApiKeyListView> listForCurrentOwner(Pageable pageable);

    /**
     * Ownership enforced; used by revoke flow to confirm existence/ownership without exposing secret.
     */
    @PreAuthorize("isAuthenticated()")
    @Query("""
           select k from ApiKey k
           where k.id = :keyId
             and k.owner.id = :#{principal.id}
           """)
    Optional<ApiKey> findOwnedById(@Param("keyId") UUID keyId);

    /**
     * Revoke with ownership predicate and named params only.
     */
    @PreAuthorize("isAuthenticated()")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update ApiKey k
              set k.status = :status,
                  k.revokedAt = :revokedAt
            where k.id = :keyId
              and k.owner.id = :#{principal.id}
           """)
    int revokeOwned(@Param("keyId") UUID keyId,
                    @Param("status") ApiKeyStatus status,
                    @Param("revokedAt") Instant revokedAt);
}
