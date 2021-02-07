package com.tmb.test.register.exception;

public class UserRefCodeNotFoundException extends RuntimeException {
    public UserRefCodeNotFoundException() { }
    public UserRefCodeNotFoundException(String message) {
        super(message);
    }
    public UserRefCodeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
