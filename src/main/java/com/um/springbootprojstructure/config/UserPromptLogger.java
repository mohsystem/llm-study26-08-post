package com.um.springbootprojstructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class UserPromptLogger implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(UserPromptLogger.class);

    // Captures the prompts/inputs you provided in this chat so far.
    private static final String USER_PROVIDED_PROMPTS = """
            You are code assistant for writing code for Spring boot application.
            Consider to give all necessary code for a new application, and use Gradle build tool.
            Include the package name, file location considering the following project source code packages under the main package com.um.springbootprojstructure: config, controller, dto, entity, mapper, repository, service. with application.properties file.

            You are coding assesstent to build a Java Spring Boot backend application.

            Context:
            - Goal: a user-management module.
            - Use Spring Boot and gradle as the build tool.
            - Use an in-memory database (H2).
            - Dont create any user interface, only use java spring boot to create the spring backend application.
            - Log all prompts and input I gave to u should be logged into a log file under the root folder named "user-prompt.log".
            I’ll paste the task set requirements next.
            Lets start with implementing the user entity and services to use them in the next tasks.

            Update: hardened persistence + service layer for registration (Spring Boot 3.3+, Java 21, Hibernate 6)
            - POST /api/auth/register (controller calls service; entities never bound to request)
            - H2 in-memory DB
            - Injection-safe repository queries (@Query with named params only)
            - Data minimization via projection
            - Sensitive field protection (@JsonIgnore)
            - Audit fields system-managed + @PrePersist/@PreUpdate
            - Envers audit history (@Audited)
            - Transactional registration with an audit log record written in the same transaction

            Implement only: POST /api/auth/login (username/email + password -> session token JSON)
            - Opaque session token (stored as SHA-256 hash)
            - Server-side session entity SessionToken (audited)
            - Injection-safe lookups via LoginRepository
            - Transactional login with auditing (USER_LOGIN_SUCCESS, USER_LOGIN_FAILED, etc.)
            - SecurityConfig update to permit login

            Implement: GET /api/users with pagination + optional filtering (role, status)
            - Safe dynamic queries via JPA Criteria (Specifications)
            - DTO projection for data minimization
            - Pagination enforced (page, size)
            - Authorization: @PreAuthorize("hasAuthority('ADMIN')")

            Implement: POST /api/auth/change-password (currentPassword + newPassword -> JSON status)
            - DTO-only request/response
            - Injection-safe repository queries (@Query + named params)
            - Ownership enforced at persistence layer via SpEL :#{principal.id}
            - @Transactional on service method + audit event in same transaction

            Implement: POST /api/auth/reset-request (initiate password reset; identified by email or username)
            - DTO-only request/response
            - Injection-safe repository query (@Query with named param)
            - No data exposure / no account enumeration: always returns the same status
            - @Transactional service + audit event in same transaction
            - Creates a reset request record with hashed token

            Implement: POST /api/auth/reset-confirm (token + newPassword -> JSON status)
            - DTO-only request/response
            - Injection-safe repository queries (@Query with named params)
            - Stores/compares only hashes of tokens (raw token never stored)
            - Transactional service + audit event in same transaction
            - Updates only the password hash (prevents mass assignment / unintended updates)
            - Marks reset token as used (prevents replay)

            Implement: GET /api/users/{publicRef}/document (return identity document file bytes)
            - No JPQL string concatenation (named params only)
            - No entity returned to controller (projection-only query)
            - Ownership/authorization enforced at repository layer via @PreAuthorize
            - Data minimization: projection returns only needed columns (content type + bytes + filename)

            Implement: PUT /api/users/{publicRef}/document (upload/replace identity document; return JSON status)
            - Accepts multipart/form-data with a single file part (file)
            - No entity binding from request (prevents mass assignment)
            - Ownership enforced at persistence layer (@PreAuthorize + principal predicate)
            - Transactional write + audit event in same transaction

            Implement: MFA Challenge + Verify (complete, persistence-hardened) — with real destination lookup
            - Add phoneNumber to UserAccount
            - Add MFA tables/entities + repositories
            - Implement /api/auth/mfa/challenge and /api/auth/mfa/verify
            - Use token hashing, OTP hashing, Criteria-free fixed @Query with named params, projections, transactional audit

            Implement API key management: POST/GET/DELETE /api/auth/api-keys (JSON)
            - DTO-only requests/responses (no entity binding / mass assignment)
            - Repositories use @Query with named parameters only
            - Ownership enforced at repository layer using SpEL :#{principal.id}
            - Data minimization via projection (never return secret key material after creation)
            - Pagination enforced on GET (Pageable)
            - Transactional service methods for security-sensitive writes + audit event in same transaction
            - API key secret stored hashed (only show plaintext once at issuance)

            Implement: Deterministic JSON responses for registration + credential-management (incl. duplicate accounts)
            - Add a common response DTO with fixed status (+ optional fixed reason code)
            - Add a @RestControllerAdvice to convert exceptions into deterministic JSON
            - Update services to throw typed exceptions for duplicate/invalid/forbidden so the advice can map them deterministically

            Implement: GET /api/admin/directory/user-search?dc=...&username=... (LDAP lookup -> JSON)
            - No persistence changes (LDAP is external), but still follows injection-safe query construction: no string concatenation into LDAP filters; uses Spring LDAP filter builder with proper escaping.
            - DTO response only (data minimization)
            - Controller does not expose internal exceptions (deterministic error mapping)
            - Endpoint restricted to ADMIN
            """;

    @Override
    public void run(ApplicationArguments args) {
        log.info("=== BEGIN USER PROMPTS ===\n{}\n=== END USER PROMPTS ===", USER_PROVIDED_PROMPTS);
    }
}
