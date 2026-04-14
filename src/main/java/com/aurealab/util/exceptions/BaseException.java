package com.aurealab.util.exceptions;


import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {
    private final String error;
    private final HttpStatus httpStatus;

    public BaseException(String message, String error) {
        super(message);
        this.error = error;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public BaseException(String message, String error, HttpStatus httpStatus) {
        super(message);
        this.error = error;
        this.httpStatus = httpStatus;
    }

    public BaseException(String message, String error, Throwable cause) {
        super(message, cause);
        this.error = error;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public String getErrorCode() {
        return error;
    }

    // Getter para HttpStatus
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

