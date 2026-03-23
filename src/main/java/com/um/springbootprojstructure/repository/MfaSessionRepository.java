package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.MfaStatus;
import com.um.springbootprojstructure.entity.SessionToken;
import com.um.springbootprojstructure.repository.projection.SessionForMfaView;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface MfaSessionRepository extends Repository<SessionToken, UUID> {

    @PreAuthorize("permitAll()")
    @Query("""
           select s.id as id,
                  s.user.id as userId,
                  s.user.phoneNumber as phoneNumber,
                  s.mfaStatus as mfaStatus,
                  s.expiresAt as expiresAt,
                  s.revoked as revoked
           from SessionToken s
           where s.tokenHash = :tokenHash
           """)
    Optional<SessionForMfaView> findSessionForMfaByTokenHash(@Param("tokenHash") String tokenHash);

    @PreAuthorize("permitAll()")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update SessionToken s
              set s.mfaStatus = :mfaStatus,
                  s.mfaVerifiedAt = :verifiedAt
            where s.id = :sessionId
           """)
    int updateMfaStatus(@Param("sessionId") UUID sessionId,
                        @Param("mfaStatus") MfaStatus mfaStatus,
                        @Param("verifiedAt") Instant verifiedAt);
}
