package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.UserAccount;
import com.um.springbootprojstructure.repository.projection.UserAccountPublicView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository extends Repository<UserAccount, UUID> {

    // Save is needed by service; not exposed directly to controller.
    @PreAuthorize("permitAll()") // registration is anonymous; service applies validation
    UserAccount save(UserAccount entity);

    @PreAuthorize("hasAuthority('ADMIN')") // example: only admin can check existence directly
    @Query("select (count(u) > 0) from UserAccount u where lower(u.username) = lower(:username)")
    boolean existsByUsernameIgnoreCase(@Param("username") String username);

    @PreAuthorize("hasAuthority('ADMIN')")
    @Query("select (count(u) > 0) from UserAccount u where lower(u.email) = lower(:email)")
    boolean existsByEmailIgnoreCase(@Param("email") String email);

    /**
     * Projection query (minimized fields). Example for later usage.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @Query("""
           select u.id as id, u.username as username, u.email as email, u.active as active
           from UserAccount u
           where u.id = :id
           """)
    Optional<UserAccountPublicView> findPublicViewById(@Param("id") UUID id);

    /**
     * Example paginated search (no unbounded list).
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @Query("""
           select u.id as id, u.username as username, u.email as email, u.active as active
           from UserAccount u
           where (:q is null or lower(u.username) like lower(concat('%', :q, '%'))
                  or lower(u.email) like lower(concat('%', :q, '%')))
           """)
    Page<UserAccountPublicView> searchPublicViews(@Param("q") String q, Pageable pageable);

    /**
     * Internal-only lookup for authentication flows; returns entity due to passwordHash requirement.
     * Not to be returned by controllers.
     */
    @PreAuthorize("hasAuthority('INTERNAL_AUTH') or hasAuthority('ADMIN')")
    @Query("select u from UserAccount u where lower(u.username) = lower(:username)")
    Optional<UserAccount> findForAuthByUsername(@Param("username") String username);

    @PreAuthorize("hasAuthority('INTERNAL_AUTH') or hasAuthority('ADMIN')")
    @Query("select u from UserAccount u where lower(u.email) = lower(:email)")
    Optional<UserAccount> findForAuthByEmail(@Param("email") String email);
}
