package com.tmb.test.register.exception;

public class MemberTypeNotAllowException extends RuntimeException {
    public MemberTypeNotAllowException() { }
    public MemberTypeNotAllowException(String message) {
        super(message);
    }
    public MemberTypeNotAllowException(String message, Throwable cause) {
        super(message, cause);
    }
}