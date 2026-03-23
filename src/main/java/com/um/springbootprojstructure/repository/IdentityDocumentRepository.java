package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.IdentityDocument;
import com.um.springbootprojstructure.repository.projection.IdentityDocumentDownloadView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;
import java.util.UUID;

public interface IdentityDocumentRepository extends Repository<IdentityDocument, UUID> {

    /**
     * Data minimization: projection only.
     * Injection-safe: named param.
     *
     * Authorization:
     * - allow ADMIN to download any user's document
     * - allow user to download ONLY their own document (principal.publicRef must exist)
     */
    @PreAuthorize("hasAuthority('ADMIN') or #publicRef == principal.publicRef")
    @Query("""
           select d.fileName as fileName,
                  d.contentType as contentType,
                  d.content as content
           from IdentityDocument d
           where d.user.publicRef = :publicRef
           """)
    Optional<IdentityDocumentDownloadView> findDownloadViewByUserPublicRef(@Param("publicRef") String publicRef);
}
