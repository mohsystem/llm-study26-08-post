package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.MfaChallenge;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface MfaChallengeRepository extends Repository<MfaChallenge, UUID> {

    @PreAuthorize("permitAll()")
    MfaChallenge save(MfaChallenge entity);

    @PreAuthorize("permitAll()")
    @Query("""
           select c from MfaChallenge c
           where c.session.id = :sessionId
             and c.verified = false
             and c.expiresAt > :now
           order by c.createdAt desc
           """)
    Optional<MfaChallenge> findLatestActiveBySession(@Param("sessionId") UUID sessionId,
                                                    @Param("now") Instant now);

    @PreAuthorize("permitAll()")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update MfaChallenge c
              set c.attempts = c.attempts + 1
            where c.id = :challengeId
           """)
    int incrementAttempts(@Param("challengeId") UUID challengeId);

    @PreAuthorize("permitAll()")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update MfaChallenge c
              set c.verified = true
            where c.id = :challengeId
           """)
    int markVerified(@Param("challengeId") UUID challengeId);
}
