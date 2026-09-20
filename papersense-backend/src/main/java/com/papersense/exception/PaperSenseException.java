package com.papersense.exception;

import org.springframework.http.HttpStatus;

/**
 * Base custom exception for PaperSense. Carries an HTTP status so the
 * GlobalExceptionHandler can return the right response code.
 */
public class PaperSenseException extends RuntimeException {

    private final HttpStatus status;

    public PaperSenseException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
