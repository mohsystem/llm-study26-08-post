package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.UserAccount;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.UUID;

public interface UserAccountPasswordUpdateRepository extends Repository<UserAccount, UUID> {

    /**
     * Update only passwordHash for the specific account id from the reset request.
     * Named params only -> injection-safe.
     */
    @PreAuthorize("permitAll()")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update UserAccount u
              set u.passwordHash = :passwordHash
            where u.id = :userId
           """)
    int updatePasswordHashByUserId(@Param("userId") UUID userId,
                                   @Param("passwordHash") String passwordHash);
}
