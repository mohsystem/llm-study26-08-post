package com.um.springbootprojstructure.mapper;

import com.um.springbootprojstructure.dto.UserCreateRequest;
import com.um.springbootprojstructure.dto.UserResponse;
import com.um.springbootprojstructure.dto.UserUpdateRequest;
import com.um.springbootprojstructure.entity.User;

public final class UserMapper {

    private UserMapper() {}

    public static User toEntity(UserCreateRequest req) {
        User u = new User();
        u.setFirstName(req.getFirstName());
        u.setLastName(req.getLastName());
        u.setEmail(req.getEmail());
        u.setActive(true);
        return u;
    }

    public static void updateEntity(User u, UserUpdateRequest req) {
        u.setFirstName(req.getFirstName());
        u.setLastName(req.getLastName());
        u.setEmail(req.getEmail());
        u.setActive(req.isActive());
    }

    public static UserResponse toResponse(User u) {
        UserResponse r = new UserResponse();
        r.setId(u.getId());
        r.setFirstName(u.getFirstName());
        r.setLastName(u.getLastName());
        r.setEmail(u.getEmail());
        r.setActive(u.isActive());
        r.setCreatedAt(u.getCreatedAt());
        r.setUpdatedAt(u.getUpdatedAt());
        return r;
    }
}
