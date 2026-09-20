package com.papersense.dto;

import java.time.LocalDateTime;

/**
 * Lightweight representation of a Paper used in list views.
 * Deliberately excludes the full extracted text to keep the payload small.
 */
public class PaperSummary {

    private Long id;
    private String title;
    private String fileName;
    private LocalDateTime uploadedAt;
    private boolean analyzed;

    public PaperSummary() {
    }

    public PaperSummary(Long id, String title, String fileName, LocalDateTime uploadedAt, boolean analyzed) {
        this.id = id;
        this.title = title;
        this.fileName = fileName;
        this.uploadedAt = uploadedAt;
        this.analyzed = analyzed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public boolean isAnalyzed() {
        return analyzed;
    }

    public void setAnalyzed(boolean analyzed) {
        this.analyzed = analyzed;
    }
}
