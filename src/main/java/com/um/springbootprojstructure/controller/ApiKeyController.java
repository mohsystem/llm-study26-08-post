package com.um.springbootprojstructure.controller;

import com.um.springbootprojstructure.dto.ApiKeyIssueRequest;
import com.um.springbootprojstructure.dto.ApiKeyIssueResponse;
import com.um.springbootprojstructure.dto.ApiKeyRevokeResponse;
import com.um.springbootprojstructure.repository.projection.ApiKeyListView;
import com.um.springbootprojstructure.service.ApiKeyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth/api-keys")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiKeyIssueResponse issue(@Valid @RequestBody ApiKeyIssueRequest request) {
        return apiKeyService.issue(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ApiKeyListView> list(Pageable pageable) {
        return apiKeyService.list(pageable);
    }

    @DeleteMapping("/{keyId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiKeyRevokeResponse revoke(@PathVariable UUID keyId) {
        return apiKeyService.revoke(keyId);
    }
}
