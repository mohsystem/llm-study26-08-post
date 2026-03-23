package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.UserListItemResponse;
import com.um.springbootprojstructure.entity.AccountStatus;
import com.um.springbootprojstructure.entity.UserRole;
import org.springframework.data.domain.Page;

public interface UserQueryService {
    Page<UserListItemResponse> listUsers(int page, int size, UserRole role, AccountStatus status);
}
