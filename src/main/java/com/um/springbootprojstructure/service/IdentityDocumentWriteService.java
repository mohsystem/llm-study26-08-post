package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.DocumentUpdateResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IdentityDocumentWriteService {
    DocumentUpdateResponse uploadOrReplace(String publicRef, MultipartFile file);
}
