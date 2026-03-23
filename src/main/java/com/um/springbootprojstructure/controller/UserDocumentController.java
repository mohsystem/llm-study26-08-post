package com.um.springbootprojstructure.controller;

import com.um.springbootprojstructure.dto.DocumentUpdateResponse;
import com.um.springbootprojstructure.repository.projection.IdentityDocumentDownloadView;
import com.um.springbootprojstructure.service.IdentityDocumentService;
import com.um.springbootprojstructure.service.IdentityDocumentWriteService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserDocumentController {

    private final IdentityDocumentService identityDocumentService;
    private final IdentityDocumentWriteService identityDocumentWriteService;

    public UserDocumentController(IdentityDocumentService identityDocumentService,
                                 IdentityDocumentWriteService identityDocumentWriteService) {
        this.identityDocumentService = identityDocumentService;
        this.identityDocumentWriteService = identityDocumentWriteService;
    }

    @GetMapping("/{publicRef}/document")
    public ResponseEntity<byte[]> downloadIdentityDocument(@PathVariable String publicRef) {
        IdentityDocumentDownloadView doc = identityDocumentService.getDocumentByUserPublicRef(publicRef);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(doc.getContentType()));
        headers.setContentDisposition(ContentDisposition.attachment().filename(doc.getFileName()).build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(doc.getContent());
    }

    @PutMapping(path = "/{publicRef}/document", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.OK)
    public DocumentUpdateResponse uploadOrReplaceDocument(@PathVariable String publicRef,
                                                         @RequestPart("file") MultipartFile file) {
        return identityDocumentWriteService.uploadOrReplace(publicRef, file);
    }
}
