package com.um.springbootprojstructure.config;

import com.um.springbootprojstructure.dto.OperationResultResponse;
import com.um.springbootprojstructure.service.exception.DuplicateAccountException;
import com.um.springbootprojstructure.service.exception.InvalidCredentialsException;
import com.um.springbootprojstructure.service.exception.InvalidOperationException;
import com.um.springbootprojstructure.service.exception.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class DeterministicExceptionHandler {

    /**
     * Registration duplicate account -> deterministic REJECTED + DUPLICATE_ACCOUNT.
     */
    @ExceptionHandler(DuplicateAccountException.class)
    public ResponseEntity<OperationResultResponse> handleDuplicate(DuplicateAccountException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new OperationResultResponse("REJECTED", "DUPLICATE_ACCOUNT"));
    }

    /**
     * Login invalid -> deterministic INVALID_CREDENTIALS.
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<OperationResultResponse> handleInvalidCreds(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new OperationResultResponse("REJECTED", "INVALID_CREDENTIALS"));
    }

    /**
     * Reset confirm invalid token etc.
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<OperationResultResponse> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new OperationResultResponse("REJECTED", "INVALID_TOKEN"));
    }

    /**
     * Generic rejected operation without leaking internal details.
     */
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<OperationResultResponse> handleInvalidOperation(InvalidOperationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new OperationResultResponse("REJECTED", "INVALID_OPERATION"));
    }

    /**
     * Validation errors -> deterministic INVALID_INPUT.
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<OperationResultResponse> handleValidation(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new OperationResultResponse("REJECTED", "INVALID_INPUT"));
    }

    /**
     * Fallback (avoid leaking exception message).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<OperationResultResponse> handleUnknown(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new OperationResultResponse("REJECTED", "INTERNAL_ERROR"));
    }
}
