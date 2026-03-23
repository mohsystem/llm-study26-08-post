package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.UserAccount;
import com.um.springbootprojstructure.repository.projection.UserAccountListView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.UUID;

public interface UserAccountQueryRepository extends Repository<UserAccount, UUID>, JpaSpecificationExecutor<UserAccount> {

    /**
     * Data minimization: return projection only.
     * Pagination enforced: Pageable required.
     *
     * NOTE: This uses Spring Data's "findAll(spec, pageable)" from JpaSpecificationExecutor,
     * which is Criteria-based (safe from injection). This declared method provides a projection variant.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @Query("""
           select u.id as id,
                  u.username as username,
                  u.email as email,
                  u.role as role,
                  u.status as status,
                  u.createdAt as createdAt
           from UserAccount u
           """)
    Page<UserAccountListView> findAllProjectedBy(Pageable pageable);
}
