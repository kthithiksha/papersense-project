package com.papersense.exception;

import org.springframework.http.HttpStatus;

/** Thrown when the AI API call fails or returns a response we cannot parse. */
public class AIServiceException extends PaperSenseException {
    public AIServiceException(String message) {
        super(message, HttpStatus.BAD_GATEWAY);
    }
}
