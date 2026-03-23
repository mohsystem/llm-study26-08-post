package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.UserAccount;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountPasswordRepository extends Repository<UserAccount, UUID> {

    /**
     * Ownership at persistence layer:
     * Only allow fetching the account that belongs to the authenticated principal.
     *
     * Requires principal.id to be a UUID. (Your security setup must supply it.)
     */
    @PreAuthorize("isAuthenticated()")
    @Query("select u from UserAccount u where u.id = :#{principal.id}")
    Optional<UserAccount> findCurrentUserForPasswordChange();

    /**
     * Persist only the specific change (password hash).
     * Named parameters -> injection-safe.
     */
    @PreAuthorize("isAuthenticated()")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update UserAccount u
              set u.passwordHash = :passwordHash
            where u.id = :#{principal.id}
           """)
    int updatePasswordHashForCurrentUser(@Param("passwordHash") String passwordHash);
}
