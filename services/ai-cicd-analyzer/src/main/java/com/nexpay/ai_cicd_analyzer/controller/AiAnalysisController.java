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

        System.out.println();
        System.out.println("================================================");
        System.out.println("       AI CI/CD ANALYZER - LOG RECEIVED");
        System.out.println("================================================");
        System.out.println("Service Name  : " + request.getServiceName());
        System.out.println("Build Number  : " + request.getBuildNumber());

        String buildLog = request.getBuildLog();

        System.out.println("Build Log Size: "
                + (buildLog != null ? buildLog.length() : 0)
                + " characters");

        System.out.println("================================================");

        String relevantLogs =
                jenkinsLogProcessor.extractRelevantLogs(buildLog);

        System.out.println("Relevant Log Size: "
                + (relevantLogs != null ? relevantLogs.length() : 0)
                + " characters");

        System.out.println("Sending relevant logs to AI analysis...");
        System.out.println("================================================");

        AiAnalysisResponse response =
                aiAnalysisService.analyze(relevantLogs);

        System.out.println("AI analysis completed.");
        System.out.println("================================================");
        System.out.println();

        return response;
    }
}

