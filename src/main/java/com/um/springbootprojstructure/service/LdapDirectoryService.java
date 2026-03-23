package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.LdapUserSearchResult;

public interface LdapDirectoryService {
    LdapUserSearchResult searchUser(String dc, String username);
}
