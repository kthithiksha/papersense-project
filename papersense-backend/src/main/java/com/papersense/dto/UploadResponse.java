package com.papersense.dto;

public class UploadResponse {

    private Long paperId;
    private String fileName;
    private String message;

    public UploadResponse() {
    }

    public UploadResponse(Long paperId, String fileName, String message) {
        this.paperId = paperId;
        this.fileName = fileName;
        this.message = message;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
