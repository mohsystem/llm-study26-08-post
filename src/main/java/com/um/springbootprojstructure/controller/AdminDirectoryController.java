package com.um.springbootprojstructure.controller;

import com.um.springbootprojstructure.dto.LdapUserSearchResult;
import com.um.springbootprojstructure.service.LdapDirectoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/directory")
public class AdminDirectoryController {

    private final LdapDirectoryService ldapDirectoryService;

    public AdminDirectoryController(LdapDirectoryService ldapDirectoryService) {
        this.ldapDirectoryService = ldapDirectoryService;
    }

    @GetMapping("/user-search")
    @ResponseStatus(HttpStatus.OK)
    public LdapUserSearchResult userSearch(@RequestParam("dc") String dc,
                                          @RequestParam("username") String username) {
        return ldapDirectoryService.searchUser(dc, username);
    }
}
