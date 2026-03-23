package com.um.springbootprojstructure.service.exception;

public class DuplicateAccountException extends RuntimeException {
    public DuplicateAccountException() {
        super("Duplicate account");
    }
}
