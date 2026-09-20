package com.papersense.controller;

import com.papersense.dto.AnalysisResponse;
import com.papersense.dto.PaperSummary;
import com.papersense.dto.UploadResponse;
import com.papersense.service.PaperService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Main REST controller exposing PaperSense's core endpoints:
 * upload, analyze, fetch analysis, list papers, delete paper.
 */
@RestController
@RequestMapping("/api/papers")
public class PaperController {

    private final PaperService paperService;

    public PaperController(PaperService paperService) {
        this.paperService = paperService;
    }

    /**
     * POST /api/papers/upload
     * multipart/form-data with a "paper" field containing the PDF file.
     */
    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadPaper(@RequestParam("paper") MultipartFile paper) {
        UploadResponse response = paperService.uploadPaper(paper);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/papers/{id}/analyze
     * Extracts text (already stored), sends it to the AI, saves and returns the analysis.
     */
    @PostMapping("/{id}/analyze")
    public ResponseEntity<AnalysisResponse> analyzePaper(@PathVariable Long id) {
        AnalysisResponse response = paperService.analyzePaper(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/papers/{id}/analysis
     */
    @GetMapping("/{id}/analysis")
    public ResponseEntity<AnalysisResponse> getAnalysis(@PathVariable Long id) {
        AnalysisResponse response = paperService.getAnalysis(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/papers
     */
    @GetMapping
    public ResponseEntity<List<PaperSummary>> getAllPapers() {
        return ResponseEntity.ok(paperService.getAllPapers());
    }

    /**
     * DELETE /api/papers/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaper(@PathVariable Long id) {
        paperService.deletePaper(id);
        return ResponseEntity.noContent().build();
    }
}
