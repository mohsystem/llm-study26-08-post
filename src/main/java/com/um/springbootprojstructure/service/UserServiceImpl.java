package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.UserCreateRequest;
import com.um.springbootprojstructure.dto.UserResponse;
import com.um.springbootprojstructure.dto.UserUpdateRequest;
import com.um.springbootprojstructure.entity.User;
import com.um.springbootprojstructure.mapper.UserMapper;
import com.um.springbootprojstructure.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DataIntegrityViolationException("Email already exists: " + request.getEmail());
        }
        User saved = userRepository.save(UserMapper.toEntity(request));
        return UserMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found. id=" + id));
        return UserMapper.toResponse(u);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toResponse).toList();
    }

    @Override
    public UserResponse update(Long id, UserUpdateRequest request) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found. id=" + id));

        // handle email uniqueness if changed
        if (!u.getEmail().equalsIgnoreCase(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new DataIntegrityViolationException("Email already exists: " + request.getEmail());
        }

        UserMapper.updateEntity(u, request);
        User saved = userRepository.save(u);
        return UserMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found. id=" + id);
        }
        userRepository.deleteById(id);
    }
}
