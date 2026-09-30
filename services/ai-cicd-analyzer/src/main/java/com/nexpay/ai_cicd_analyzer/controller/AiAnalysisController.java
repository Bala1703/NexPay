package com.nexpay.ai_cicd_analyzer.controller;

import com.nexpay.ai_cicd_analyzer.dto.BuildLogRequest;
import com.nexpay.ai_cicd_analyzer.model.AiAnalysisResponse;
import com.nexpay.ai_cicd_analyzer.service.AiAnalysisService;
import com.nexpay.ai_cicd_analyzer.service.JenkinsLogProcessor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analyze")
public class AiAnalysisController {

    private final AiAnalysisService aiAnalysisService;
    private final JenkinsLogProcessor jenkinsLogProcessor;

    public AiAnalysisController(
            AiAnalysisService aiAnalysisService,
            JenkinsLogProcessor jenkinsLogProcessor) {

        this.aiAnalysisService = aiAnalysisService;
        this.jenkinsLogProcessor = jenkinsLogProcessor;
    }

    @PostMapping
    public AiAnalysisResponse analyze(@RequestBody BuildLogRequest request) {

        String relevantLogs =
                jenkinsLogProcessor.extractRelevantLogs(request.getBuildLog());

        return aiAnalysisService.analyze(relevantLogs);
    }
}