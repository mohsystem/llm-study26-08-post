package com.um.springbootprojstructure.service.exception;

public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException() {
        super("Invalid operation");
    }

    public InvalidOperationException(String message) {
        super(message);
    }
}
