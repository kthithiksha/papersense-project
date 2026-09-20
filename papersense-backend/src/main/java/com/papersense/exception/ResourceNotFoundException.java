package com.papersense.exception;

import org.springframework.http.HttpStatus;

/** Thrown when a requested Paper or Analysis does not exist. */
public class ResourceNotFoundException extends PaperSenseException {
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
