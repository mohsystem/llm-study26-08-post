package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.UserListItemResponse;
import com.um.springbootprojstructure.entity.AccountStatus;
import com.um.springbootprojstructure.entity.UserAccount;
import com.um.springbootprojstructure.entity.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserQueryServiceImpl implements UserQueryService {

    private final EntityManager em;

    public UserQueryServiceImpl(EntityManager em) {
        this.em = em;
    }

    /**
     * Read-only query. Pagination enforced.
     * Uses Criteria API (no JPQL string building) -> injection-resistant.
     */
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<UserListItemResponse> listUsers(int page, int size, UserRole role, AccountStatus status) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100); // hard cap to limit data exfiltration

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // -------- data query (DTO columns only) --------
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<UserAccount> u = cq.from(UserAccount.class);

        List<Predicate> predicates = new ArrayList<>();
        if (role != null) {
            predicates.add(cb.equal(u.get("role"), role));
        }
        if (status != null) {
            predicates.add(cb.equal(u.get("status"), status));
        }

        cq.select(cb.array(
                u.get("id"),
                u.get("username"),
                u.get("email"),
                u.get("role"),
                u.get("status"),
                u.get("createdAt")
        ));

        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(Predicate[]::new)));
        }

        // stable ordering for pagination
        cq.orderBy(cb.asc(u.get("createdAt")), cb.asc(u.get("id")));

        TypedQuery<Object[]> query = em.createQuery(cq);
        query.setFirstResult(safePage * safeSize);
        query.setMaxResults(safeSize);

        List<Object[]> rows = query.getResultList();
        List<UserListItemResponse> content = rows.stream().map(r -> {
            UserListItemResponse dto = new UserListItemResponse();
            dto.setId((UUID) r[0]);
            dto.setUsername((String) r[1]);
            dto.setEmail((String) r[2]);
            dto.setRole((UserRole) r[3]);
            dto.setStatus((AccountStatus) r[4]);
            dto.setCreatedAt((java.time.Instant) r[5]);
            return dto;
        }).toList();

        // -------- count query --------
        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<UserAccount> cu = countCq.from(UserAccount.class);

        List<Predicate> countPreds = new ArrayList<>();
        if (role != null) {
            countPreds.add(cb.equal(cu.get("role"), role));
        }
        if (status != null) {
            countPreds.add(cb.equal(cu.get("status"), status));
        }
        countCq.select(cb.count(cu));
        if (!countPreds.isEmpty()) {
            countCq.where(cb.and(countPreds.toArray(Predicate[]::new)));
        }

        long total = em.createQuery(countCq).getSingleResult();

        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Order.asc("createdAt"), Sort.Order.asc("id")));
        return new PageImpl<>(content, pageable, total);
    }
}
