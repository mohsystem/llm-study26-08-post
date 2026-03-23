package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.LdapUserSearchResponse;
import com.um.springbootprojstructure.dto.LdapUserSearchResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.List;
import java.util.Locale;

@Service
public class LdapDirectoryServiceImpl implements LdapDirectoryService {

    private final LdapTemplate ldapTemplate;

    @Value("${app.ldap.baseTemplate}")
    private String baseTemplate;

    public LdapDirectoryServiceImpl(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    /**
     * Read-only external directory lookup (no DB transaction needed, but keep readOnly semantics).
     * Restrict to admin at service layer (defense in depth).
     */
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ADMIN')")
    public LdapUserSearchResult searchUser(String dc, String username) {
        String normalizedDc = normalizeDcOrThrow(dc);
        String normalizedUsername = normalizeUsernameOrThrow(username);

        // Construct base DN via template (not in filter); dc is validated strictly.
        String searchBase = String.format(baseTemplate, normalizedDc);

        // Injection-safe filter: user input is escaped by Spring LDAP filter implementations.
        // Choose attribute according to your directory schema; "uid" is common.
        EqualsFilter filter = new EqualsFilter("uid", normalizedUsername);

        List<LdapUserSearchResponse> results = ldapTemplate.search(
                searchBase,
                filter.encode(),
                (AttributesMapper<LdapUserSearchResponse>) attrs -> map(attrs)
        );

        return new LdapUserSearchResult("OK", results);
    }

    private static LdapUserSearchResponse map(Attributes attrs) throws NamingException {
        LdapUserSearchResponse dto = new LdapUserSearchResponse();
        dto.setUsername(get(attrs, "uid"));
        dto.setDisplayName(get(attrs, "cn"));
        dto.setEmail(get(attrs, "mail"));
        return dto;
    }

    private static String get(Attributes attrs, String key) throws NamingException {
        return attrs.get(key) == null ? null : String.valueOf(attrs.get(key).get());
    }

    /**
     * dc is used to build a DN component; strictly allow alphanumerics and hyphen only.
     */
    private static String normalizeDcOrThrow(String dc) {
        if (dc == null) throw new IllegalArgumentException("dc is required");
        String v = dc.trim().toLowerCase(Locale.ROOT);
        if (!v.matches("^[a-z0-9-]{1,63}$")) {
            throw new IllegalArgumentException("invalid dc");
        }
        return v;
    }

    /**
     * username used in filter; validate length and allowed chars to reduce abuse.
     */
    private static String normalizeUsernameOrThrow(String username) {
        if (username == null) throw new IllegalArgumentException("username is required");
        String v = username.trim();
        if (v.length() < 1 || v.length() > 64) {
            throw new IllegalArgumentException("invalid username");
        }
        // Allow common uid patterns
        if (!v.matches("^[A-Za-z0-9._-]+$")) {
            throw new IllegalArgumentException("invalid username");
        }
        return v;
    }
}
