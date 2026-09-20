package com.papersense.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents an uploaded research paper.
 * The extracted plain text is stored so we don't need to re-read the PDF
 * every time we run analysis.
 */
@Entity
@Table(name = "papers")
public class Paper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    @Column(columnDefinition = "TEXT")
    private String extractedText;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    public Paper() {
    }

    public Paper(String title, String fileName, String filePath, String extractedText) {
        this.title = title;
        this.fileName = fileName;
        this.filePath = filePath;
        this.extractedText = extractedText;
        this.uploadedAt = LocalDateTime.now();
    }

    // ---------- Getters and Setters ----------

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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
