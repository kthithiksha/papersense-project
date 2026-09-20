package com.papersense.controller;

import com.papersense.dto.AnalysisResponse;
import com.papersense.exception.ResourceNotFoundException;
import com.papersense.model.Analysis;
import com.papersense.repository.AnalysisRepository;
import com.papersense.service.PaperService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Secondary controller for looking up an analysis directly by its own id
 * (as opposed to PaperController's /api/papers/{id}/analysis, which looks
 * it up by paper id). Most of the frontend uses the paper-based endpoint;
 * this one is a convenience for direct analysis lookups.
 */
@RestController
@RequestMapping("/api/analyses")
public class AnalysisController {

    private final AnalysisRepository analysisRepository;
    private final PaperService paperService;

    public AnalysisController(AnalysisRepository analysisRepository, PaperService paperService) {
        this.analysisRepository = analysisRepository;
        this.paperService = paperService;
    }

    /**
     * GET /api/analyses/{analysisId}
     */
    @GetMapping("/{analysisId}")
    public ResponseEntity<AnalysisResponse> getByAnalysisId(@PathVariable Long analysisId) {
        Analysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Analysis not found with id: " + analysisId));

        // Reuse PaperService's analysis lookup (by paper id) to keep JSON parsing in one place.
        AnalysisResponse response = paperService.getAnalysis(analysis.getPaperId());
        return ResponseEntity.ok(response);
    }
}
