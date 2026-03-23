package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.repository.projection.IdentityDocumentDownloadView;

public interface IdentityDocumentService {
    IdentityDocumentDownloadView getDocumentByUserPublicRef(String publicRef);
}
