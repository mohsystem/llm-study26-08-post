package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.repository.IdentityDocumentRepository;
import com.um.springbootprojstructure.repository.projection.IdentityDocumentDownloadView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IdentityDocumentServiceImpl implements IdentityDocumentService {

    private final IdentityDocumentRepository identityDocumentRepository;

    public IdentityDocumentServiceImpl(IdentityDocumentRepository identityDocumentRepository) {
        this.identityDocumentRepository = identityDocumentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public IdentityDocumentDownloadView getDocumentByUserPublicRef(String publicRef) {
        return identityDocumentRepository.findDownloadViewByUserPublicRef(publicRef)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));
    }
}
