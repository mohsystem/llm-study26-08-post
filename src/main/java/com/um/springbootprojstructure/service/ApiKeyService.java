package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.ApiKeyIssueRequest;
import com.um.springbootprojstructure.dto.ApiKeyIssueResponse;
import com.um.springbootprojstructure.dto.ApiKeyRevokeResponse;
import com.um.springbootprojstructure.repository.projection.ApiKeyListView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ApiKeyService {
    ApiKeyIssueResponse issue(ApiKeyIssueRequest request);
    Page<ApiKeyListView> list(Pageable pageable);
    ApiKeyRevokeResponse revoke(UUID keyId);
}
