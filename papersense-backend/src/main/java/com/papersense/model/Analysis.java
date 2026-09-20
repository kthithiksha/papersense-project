package com.papersense.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Stores the structured AI analysis produced for a Paper.
 *
 * List-type fields (technologies, advantages, limitations, researchGaps,
 * suggestedImprovements, futureScope) are stored as JSON array strings
 * (e.g. ["item1","item2"]) inside LONGTEXT columns to keep the schema simple.
 * They are converted to/from java.util.List in the service layer using Jackson.
 */
@Entity
@Table(name = "analyses")
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "paper_id", nullable = false)
    private Long paperId;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String problemStatement;

    @Column(columnDefinition = "TEXT")
    private String technologies; // JSON array string

    @Column(columnDefinition = "TEXT")
    private String aiExplanation;

    @Column(columnDefinition = "TEXT")
    private String methodology;

    @Column(columnDefinition = "TEXT")
    private String advantages; // JSON array string

    @Column(columnDefinition = "TEXT")
    private String limitations; // JSON array string

    @Column(columnDefinition = "TEXT")
    private String researchGaps; // JSON array string

    @Column(columnDefinition = "TEXT")
    private String suggestedImprovements; // JSON array string

    @Column(columnDefinition = "TEXT")
    private String futureScope; // JSON array string

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Analysis() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ---------- Getters and Setters ----------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getProblemStatement() {
        return problemStatement;
    }

    public void setProblemStatement(String problemStatement) {
        this.problemStatement = problemStatement;
    }

    public String getTechnologies() {
        return technologies;
    }

    public void setTechnologies(String technologies) {
        this.technologies = technologies;
    }

    public String getAiExplanation() {
        return aiExplanation;
    }

    public void setAiExplanation(String aiExplanation) {
        this.aiExplanation = aiExplanation;
    }

    public String getMethodology() {
        return methodology;
    }

    public void setMethodology(String methodology) {
        this.methodology = methodology;
    }

    public String getAdvantages() {
        return advantages;
    }

    public void setAdvantages(String advantages) {
        this.advantages = advantages;
    }

    public String getLimitations() {
        return limitations;
    }

    public void setLimitations(String limitations) {
        this.limitations = limitations;
    }

    public String getResearchGaps() {
        return researchGaps;
    }

    public void setResearchGaps(String researchGaps) {
        this.researchGaps = researchGaps;
    }

    public String getSuggestedImprovements() {
        return suggestedImprovements;
    }

    public void setSuggestedImprovements(String suggestedImprovements) {
        this.suggestedImprovements = suggestedImprovements;
    }

    public String getFutureScope() {
        return futureScope;
    }

    public void setFutureScope(String futureScope) {
        this.futureScope = futureScope;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
