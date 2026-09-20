package com.papersense.dto;

import java.util.List;

/**
 * Plain DTO representing the analysis of a paper in a frontend-friendly shape.
 * List-type fields are real java.util.List here (unlike the Analysis entity,
 * which stores them as JSON strings in the database).
 */
public class AnalysisResponse {

    private Long paperId;
    private String summary;
    private String problemStatement;
    private List<String> technologies;
    private String aiExplanation;
    private String methodology;
    private List<String> advantages;
    private List<String> limitations;
    private List<String> researchGaps;
    private List<String> suggestedImprovements;
    private List<String> futureScope;

    public AnalysisResponse() {
    }

    public AnalysisResponse(Long paperId, String summary, String problemStatement,
                             List<String> technologies, String aiExplanation, String methodology,
                             List<String> advantages, List<String> limitations,
                             List<String> researchGaps, List<String> suggestedImprovements,
                             List<String> futureScope) {
        this.paperId = paperId;
        this.summary = summary;
        this.problemStatement = problemStatement;
        this.technologies = technologies;
        this.aiExplanation = aiExplanation;
        this.methodology = methodology;
        this.advantages = advantages;
        this.limitations = limitations;
        this.researchGaps = researchGaps;
        this.suggestedImprovements = suggestedImprovements;
        this.futureScope = futureScope;
    }

    // ---------- Getters and Setters ----------

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

    public List<String> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(List<String> technologies) {
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

    public List<String> getAdvantages() {
        return advantages;
    }

    public void setAdvantages(List<String> advantages) {
        this.advantages = advantages;
    }

    public List<String> getLimitations() {
        return limitations;
    }

    public void setLimitations(List<String> limitations) {
        this.limitations = limitations;
    }

    public List<String> getResearchGaps() {
        return researchGaps;
    }

    public void setResearchGaps(List<String> researchGaps) {
        this.researchGaps = researchGaps;
    }

    public List<String> getSuggestedImprovements() {
        return suggestedImprovements;
    }

    public void setSuggestedImprovements(List<String> suggestedImprovements) {
        this.suggestedImprovements = suggestedImprovements;
    }

    public List<String> getFutureScope() {
        return futureScope;
    }

    public void setFutureScope(List<String> futureScope) {
        this.futureScope = futureScope;
    }
}
