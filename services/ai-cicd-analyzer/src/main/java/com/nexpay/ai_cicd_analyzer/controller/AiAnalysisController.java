package com.nexpay.ai_cicd_analyzer.controller;

import com.nexpay.ai_cicd_analyzer.dto.BuildLogRequest;
import com.nexpay.ai_cicd_analyzer.model.AiAnalysisResponse;
import com.nexpay.ai_cicd_analyzer.service.AiAnalysisService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analyze")
public class AiAnalysisController {

    private final AiAnalysisService aiAnalysisService;

    public AiAnalysisController(AiAnalysisService aiAnalysisService) {
        this.aiAnalysisService = aiAnalysisService;
    }

    @PostMapping
    public AiAnalysisResponse analyze(@RequestBody BuildLogRequest request) {

        return aiAnalysisService.analyze(request.getBuildLog());
    }
}