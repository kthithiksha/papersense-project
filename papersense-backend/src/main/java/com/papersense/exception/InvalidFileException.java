package com.papersense.exception;

import org.springframework.http.HttpStatus;

/** Thrown when the uploaded file is missing, not a PDF, empty, or too large. */
public class InvalidFileException extends PaperSenseException {
    public InvalidFileException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
