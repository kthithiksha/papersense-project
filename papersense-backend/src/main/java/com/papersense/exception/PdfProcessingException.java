package com.papersense.exception;

import org.springframework.http.HttpStatus;

/** Thrown when PDFBox fails to extract text or the PDF has no readable content. */
public class PdfProcessingException extends PaperSenseException {
    public PdfProcessingException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
