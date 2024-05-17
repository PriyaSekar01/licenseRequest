package com.microserviceproj.exception;

import java.io.Serializable;

public class CompanyServiceException extends RuntimeException implements Serializable {

    public CompanyServiceException(String message) {
        super(message);
    }

    public CompanyServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
