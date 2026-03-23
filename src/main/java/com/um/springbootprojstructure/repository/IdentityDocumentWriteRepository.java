package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.IdentityDocument;
import com.um.springbootprojstructure.repository.projection.DocumentIdView;
import com.um.springbootprojstructure.repository.projection.UserIdView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;
import java.util.UUID;

public interface IdentityDocumentWriteRepository extends Repository<IdentityDocument, UUID> {

    /**
     * Ownership enforced at persistence layer:
     * - ADMIN can update any user's document
     * - User can update ONLY their own document (principal.publicRef required)
     */
    @PreAuthorize("hasAuthority('ADMIN') or #publicRef == principal.publicRef")
    @Query("select u.id as id from UserAccount u where u.publicRef = :publicRef")
    Optional<UserIdView> findUserIdByPublicRef(@Param("publicRef") String publicRef);

    @PreAuthorize("hasAuthority('ADMIN') or #publicRef == principal.publicRef")
    @Query("select d.id as id from IdentityDocument d where d.user.publicRef = :publicRef")
    Optional<DocumentIdView> findDocumentIdByUserPublicRef(@Param("publicRef") String publicRef);

    /**
     * Save used for insert/update. Controller never touches repository.
     */
    @PreAuthorize("hasAuthority('ADMIN') or #entity.user.publicRef == principal.publicRef")
    IdentityDocument save(@Param("entity") IdentityDocument entity);

    /**
     * Internal load by id for replacement; still owner-protected by checking user publicRef via service.
     * (No direct controller access.)
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @Query("select d from IdentityDocument d where d.id = :id")
    Optional<IdentityDocument> findByIdForAdmin(@Param("id") UUID id);
}
