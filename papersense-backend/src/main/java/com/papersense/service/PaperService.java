package com.papersense.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.papersense.dto.AnalysisResponse;
import com.papersense.dto.PaperSummary;
import com.papersense.dto.UploadResponse;
import com.papersense.exception.ResourceNotFoundException;
import com.papersense.model.Analysis;
import com.papersense.model.Paper;
import com.papersense.repository.AnalysisRepository;
import com.papersense.repository.PaperRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Core service that orchestrates the whole PaperSense workflow:
 * upload -> extract text -> AI analysis -> save -> retrieve -> delete.
 */
@Service
public class PaperService {

    private final PaperRepository paperRepository;
    private final AnalysisRepository analysisRepository;
    private final PdfService pdfService;
    private final AIService aiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PaperService(PaperRepository paperRepository,
                         AnalysisRepository analysisRepository,
                         PdfService pdfService,
                         AIService aiService) {
        this.paperRepository = paperRepository;
        this.analysisRepository = analysisRepository;
        this.pdfService = pdfService;
        this.aiService = aiService;
    }

    /**
     * Validates and stores an uploaded PDF, extracting its text immediately
     * so re-analysis never requires re-uploading the file.
     */
    public UploadResponse uploadPaper(MultipartFile file) {
        pdfService.validateFile(file);

        String filePath = pdfService.saveFile(file);
        String extractedText = pdfService.extractText(filePath);

        String title = deriveTitle(file.getOriginalFilename());
        Paper paper = new Paper(title, file.getOriginalFilename(), filePath, extractedText);
        paper = paperRepository.save(paper);

        return new UploadResponse(paper.getId(), paper.getFileName(), "Paper uploaded successfully");
    }

    /**
     * Runs (or re-runs) AI analysis for a previously uploaded paper.
     */
    public AnalysisResponse analyzePaper(Long paperId) {
        Paper paper = getPaperOrThrow(paperId);

        AIService.AnalysisResult result = aiService.analyzePaper(paper.getExtractedText());

        // Remove any previous analysis for this paper before saving the new one.
        analysisRepository.findByPaperId(paperId).ifPresent(analysisRepository::delete);

        Analysis analysis = new Analysis();
        analysis.setPaperId(paperId);
        analysis.setSummary(result.summary);
        analysis.setProblemStatement(result.problemStatement);
        analysis.setTechnologies(toJson(result.technologies));
        analysis.setAiExplanation(result.aiExplanation);
        analysis.setMethodology(result.methodology);
        analysis.setAdvantages(toJson(result.advantages));
        analysis.setLimitations(toJson(result.limitations));
        analysis.setResearchGaps(toJson(result.researchGaps));
        analysis.setSuggestedImprovements(toJson(result.suggestedImprovements));
        analysis.setFutureScope(toJson(result.futureScope));

        analysisRepository.save(analysis);

        return toAnalysisResponse(analysis);
    }

    /**
     * Retrieves the previously saved analysis for a paper.
     */
    public AnalysisResponse getAnalysis(Long paperId) {
        getPaperOrThrow(paperId); // ensures a clear "paper not found" error if applicable

        Analysis analysis = analysisRepository.findByPaperId(paperId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No analysis found for this paper yet. Please analyze it first."));

        return toAnalysisResponse(analysis);
    }

    /**
     * Lists all uploaded papers (summary view, most recent first).
     */
    public List<PaperSummary> getAllPapers() {
        List<Paper> papers = paperRepository.findAll();
        List<PaperSummary> summaries = new ArrayList<>();

        for (Paper paper : papers) {
            boolean analyzed = analysisRepository.findByPaperId(paper.getId()).isPresent();
            summaries.add(new PaperSummary(
                    paper.getId(), paper.getTitle(), paper.getFileName(), paper.getUploadedAt(), analyzed));
        }

        summaries.sort((a, b) -> b.getUploadedAt().compareTo(a.getUploadedAt()));
        return summaries;
    }

    /**
     * Deletes a paper, its analysis (if any), and the stored PDF file on disk.
     */
    public void deletePaper(Long paperId) {
        Paper paper = getPaperOrThrow(paperId);

        analysisRepository.deleteByPaperId(paperId);
        paperRepository.delete(paper);

        File file = new File(paper.getFilePath());
        if (file.exists()) {
            file.delete();
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private Paper getPaperOrThrow(Long paperId) {
        Optional<Paper> paperOpt = paperRepository.findById(paperId);
        return paperOpt.orElseThrow(() ->
                new ResourceNotFoundException("Paper not found with id: " + paperId));
    }

    private String deriveTitle(String fileName) {
        if (fileName == null) {
            return "Untitled Paper";
        }
        String withoutExtension = fileName.replaceAll("(?i)\\.pdf$", "");
        return withoutExtension.replaceAll("[_-]+", " ").trim();
    }

    private String toJson(List<String> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }

    private List<String> fromJson(String json) {
        try {
            if (json == null || json.isBlank()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(json, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, String.class));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private AnalysisResponse toAnalysisResponse(Analysis analysis) {
        return new AnalysisResponse(
                analysis.getPaperId(),
                analysis.getSummary(),
                analysis.getProblemStatement(),
                fromJson(analysis.getTechnologies()),
                analysis.getAiExplanation(),
                analysis.getMethodology(),
                fromJson(analysis.getAdvantages()),
                fromJson(analysis.getLimitations()),
                fromJson(analysis.getResearchGaps()),
                fromJson(analysis.getSuggestedImprovements()),
                fromJson(analysis.getFutureScope())
        );
    }
}
