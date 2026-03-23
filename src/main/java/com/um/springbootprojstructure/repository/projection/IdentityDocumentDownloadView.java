package com.um.springbootprojstructure.repository.projection;

public interface IdentityDocumentDownloadView {
    String getFileName();
    String getContentType();
    byte[] getContent();
}
