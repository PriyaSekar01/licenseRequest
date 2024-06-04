package com.microserviceproj.exception;

public class OtpServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OtpServiceException(String message) {
        super(message);
    }

    public OtpServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}


