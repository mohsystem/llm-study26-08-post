package com.um.springbootprojstructure.controller;

import com.um.springbootprojstructure.dto.UserListItemResponse;
import com.um.springbootprojstructure.entity.AccountStatus;
import com.um.springbootprojstructure.entity.UserRole;
import com.um.springbootprojstructure.service.UserQueryService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserQueryService userQueryService;

    public UserController(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    /**
     * GET /api/users?page=0&size=20&role=ADMIN&status=ACTIVE
     */
    @GetMapping
    public Page<UserListItemResponse> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) AccountStatus status
    ) {
        return userQueryService.listUsers(page, size, role, status);
    }
}
