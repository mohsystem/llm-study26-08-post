package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.UserCreateRequest;
import com.um.springbootprojstructure.dto.UserResponse;
import com.um.springbootprojstructure.dto.UserUpdateRequest;

import java.util.List;

public interface UserService {
    UserResponse create(UserCreateRequest request);
    UserResponse getById(Long id);
    List<UserResponse> getAll();
    UserResponse update(Long id, UserUpdateRequest request);
    void delete(Long id);
}
