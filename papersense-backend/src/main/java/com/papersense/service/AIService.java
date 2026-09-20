package com.papersense.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.papersense.exception.AIServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class AIService {

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.api.url}")
    private String apiUrl;

    @Value("${ai.api.model}")
    private String model;

    @Value("${ai.chunk.max-chars}")
    private int maxCharsPerChunk;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    public static class AnalysisResult {
        public String summary = "";
        public String problemStatement = "";
        public List<String> technologies = new ArrayList<>();
        public String aiExplanation = "";
        public String methodology = "";
        public List<String> advantages = new ArrayList<>();
        public List<String> limitations = new ArrayList<>();
        public List<String> researchGaps = new ArrayList<>();
        public List<String> suggestedImprovements = new ArrayList<>();
        public List<String> futureScope = new ArrayList<>();
    }

    public AnalysisResult analyzePaper(String paperText) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new AIServiceException(
                    "AI API key is not configured. Set the AI_API_KEY environment variable.");
        }

        List<String> chunks = splitIntoChunks(paperText, maxCharsPerChunk);

        if (chunks.size() == 1) {
            return callModelAndParse(buildSingleChunkPrompt(chunks.get(0)));
        }

        List<AnalysisResult> partialResults = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            String prompt = buildChunkPrompt(chunks.get(i), i + 1, chunks.size());
            partialResults.add(callModelAndParse(prompt));
        }

        return mergePartialResults(partialResults);
    }

    private List<String> splitIntoChunks(String text, int maxChars) {
        List<String> chunks = new ArrayList<>();

        if (text.length() <= maxChars) {
            chunks.add(text);
            return chunks;
        }

        String[] paragraphs = text.split("\n\n");
        StringBuilder current = new StringBuilder();

        for (String paragraph : paragraphs) {
            if (current.length() + paragraph.length() + 2 > maxChars) {
                if (current.length() > 0) {
                    chunks.add(current.toString());
                    current = new StringBuilder();
                }
                if (paragraph.length() > maxChars) {
                    int start = 0;
                    while (start < paragraph.length()) {
                        int end = Math.min(start + maxChars, paragraph.length());
                        chunks.add(paragraph.substring(start, end));
                        start = end;
                    }
                    continue;
                }
            }
            current.append(paragraph).append("\n\n");
        }

        if (current.length() > 0) {
            chunks.add(current.toString());
        }

        return chunks;
    }

    private AnalysisResult mergePartialResults(List<AnalysisResult> parts) {
        AnalysisResult merged = new AnalysisResult();

        StringBuilder summary = new StringBuilder();
        StringBuilder problem = new StringBuilder();
        StringBuilder aiExplanation = new StringBuilder();
        StringBuilder methodology = new StringBuilder();

        for (int i = 0; i < parts.size(); i++) {
            AnalysisResult part = parts.get(i);
            summary.append("Section ").append(i + 1).append(": ").append(part.summary).append(" ");
            problem.append(part.problemStatement).append(" ");
            aiExplanation.append(part.aiExplanation).append(" ");
            methodology.append(part.methodology).append(" ");

            merged.technologies.addAll(part.technologies);
            merged.advantages.addAll(part.advantages);
            merged.limitations.addAll(part.limitations);
            merged.researchGaps.addAll(part.researchGaps);
            merged.suggestedImprovements.addAll(part.suggestedImprovements);
            merged.futureScope.addAll(part.futureScope);
        }

        merged.summary = summary.toString().trim();
        merged.problemStatement = problem.toString().trim();
        merged.aiExplanation = aiExplanation.toString().trim();
        merged.methodology = methodology.toString().trim();

        deduplicate(merged.technologies);
        deduplicate(merged.advantages);
        deduplicate(merged.limitations);
        deduplicate(merged.researchGaps);
        deduplicate(merged.suggestedImprovements);
        deduplicate(merged.futureScope);

        return merged;
    }

    private void deduplicate(List<String> list) {
        List<String> unique = new ArrayList<>();
        for (String item : list) {
            if (item != null && !item.isBlank() && !unique.contains(item.trim())) {
                unique.add(item.trim());
            }
        }
        list.clear();
        list.addAll(unique);
    }

    private String buildSingleChunkPrompt(String paperText) {
        return basePromptInstructions() + "\n\nHere is the full research paper text:\n\n" + paperText;
    }

    private String buildChunkPrompt(String chunkText, int chunkNumber, int totalChunks) {
        return basePromptInstructions()
                + "\n\nNOTE: This is part " + chunkNumber + " of " + totalChunks
                + " of a longer research paper. Analyze only the content given below, "
                + "and it will be combined with the analysis of the other parts.\n\n"
                + chunkText;
    }

    private String basePromptInstructions() {
        return """
                You are a research assistant helping analyze an academic research paper.
                Read the paper text below carefully and respond with ONLY a valid JSON object
                (no markdown fences, no extra commentary) with exactly these fields:

                {
                  "summary": "A clear overview of what the paper is about",
                  "problemStatement": "The core problem the paper addresses",
                  "technologies": ["technology or AI technique 1", "technology or AI technique 2"],
                  "aiExplanation": "A simple, beginner-friendly explanation of the AI/ML techniques used",
                  "methodology": "A description of the methodology/approach used in the paper",
                  "advantages": ["strength 1", "strength 2"],
                  "limitations": ["limitation 1", "limitation 2"],
                  "researchGaps": ["research gap 1", "research gap 2"],
                  "suggestedImprovements": ["specific improvement 1", "specific improvement 2"],
                  "futureScope": ["future research direction 1", "future research direction 2"]
                }

                IMPORTANT RULES:
                - Base "summary", "problemStatement", "technologies", "methodology", and "advantages"
                  strictly on information explicitly present in the paper text. Do not invent facts.
                - "aiExplanation" may be your own simplified interpretation of techniques mentioned in the paper.
                - For "researchGaps", critically evaluate the paper for weaknesses such as: dataset limitations,
                  small sample size, experimental limitations, missing comparisons, missing evaluation metrics,
                  generalization problems, computational limitations, real-world deployment limitations,
                  reproducibility issues, missing experiments, outdated techniques, and missing baseline comparisons.
                  Only list gaps that are reasonably inferable from the text.
                - For "suggestedImprovements", be specific about exactly what could be changed and why.
                - "futureScope" should contain forward-looking, AI-suggested research directions (these may go
                  beyond what the paper explicitly states, but should stay logically connected to the paper's topic).
                - If a field cannot be determined from the text, use an empty string "" or empty array [].
                - Respond with ONLY the JSON object. No explanations before or after it.
                """;
    }

    private AnalysisResult callModelAndParse(String promptText) {
        String rawResponseText = callModel(promptText);
        return parseModelJson(rawResponseText);
    }

    private String callModel(String promptText) {
        try {
            // Build Gemini payload structure: contents -> parts -> text
            ObjectNode requestBody = objectMapper.createObjectNode();
            ArrayNode contentsArray = requestBody.putArray("contents");
            ObjectNode contentObj = contentsArray.addObject();
            ArrayNode partsArray = contentObj.putArray("parts");
            ObjectNode partObj = partsArray.addObject();
            partObj.put("text", promptText);

            String requestJson = objectMapper.writeValueAsString(requestBody);

            // Append apiKey to the configured Gemini endpoint URL
            String fullUrl = apiUrl + apiKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl))
                    .timeout(Duration.ofSeconds(90))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new AIServiceException(
                        "AI API returned an error (status " + response.statusCode() + "): " + response.body());
            }

            JsonNode responseBody = objectMapper.readTree(response.body());
            JsonNode candidates = responseBody.get("candidates");

            if (candidates == null || !candidates.isArray() || candidates.isEmpty()) {
                throw new AIServiceException("AI API returned an unexpected response format.");
            }

            JsonNode content = candidates.get(0).get("content");
            JsonNode parts = content != null ? content.get("parts") : null;

            if (parts == null || !parts.isArray() || parts.isEmpty()) {
                throw new AIServiceException("AI API returned an unexpected response format.");
            }

            StringBuilder textBuilder = new StringBuilder();
            for (JsonNode part : parts) {
                if (part.has("text")) {
                    textBuilder.append(part.get("text").asText());
                }
            }

            return textBuilder.toString();

        } catch (AIServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new AIServiceException("Failed to call the AI service: " + e.getMessage());
        }
    }

    private AnalysisResult parseModelJson(String rawText) {
        String cleaned = rawText.trim();

        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceFirst("^```(json)?", "").trim();
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.length() - 3).trim();
            }
        }

        try {
            JsonNode node = objectMapper.readTree(cleaned);

            AnalysisResult result = new AnalysisResult();
            result.summary = textOrEmpty(node, "summary");
            result.problemStatement = textOrEmpty(node, "problemStatement");
            result.technologies = listOrEmpty(node, "technologies");
            result.aiExplanation = textOrEmpty(node, "aiExplanation");
            result.methodology = textOrEmpty(node, "methodology");
            result.advantages = listOrEmpty(node, "advantages");
            result.limitations = listOrEmpty(node, "limitations");
            result.researchGaps = listOrEmpty(node, "researchGaps");
            result.suggestedImprovements = listOrEmpty(node, "suggestedImprovements");
            result.futureScope = listOrEmpty(node, "futureScope");

            return result;

        } catch (Exception e) {
            throw new AIServiceException(
                    "Could not parse the AI response as valid JSON. Please try analyzing again.");
        }
    }

    private String textOrEmpty(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return (value == null || value.isNull()) ? "" : value.asText("");
    }

    private List<String> listOrEmpty(JsonNode node, String field) {
        List<String> list = new ArrayList<>();
        JsonNode value = node.get(field);
        if (value != null && value.isArray()) {
            for (JsonNode item : value) {
                list.add(item.asText(""));
            }
        }
        return list;
    }
}