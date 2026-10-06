package com.nexpay.ai_cicd_analyzer.controller;

import com.nexpay.ai_cicd_analyzer.model.AiAnalysisResponse;
import com.nexpay.ai_cicd_analyzer.service.AiAnalysisService;
import com.nexpay.ai_cicd_analyzer.service.JenkinsLogProcessor;
import org.springframework.http.MediaType;
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

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
    public AiAnalysisResponse analyze(
            @RequestHeader("X-Service-Name") String serviceName,
            @RequestHeader("X-Build-Number") int buildNumber,
            @RequestBody String buildLog) {

        System.out.println();
        System.out.println("================================================");
        System.out.println("       AI CI/CD ANALYZER - LOG RECEIVED");
        System.out.println("================================================");
        System.out.println("Service Name  : " + serviceName);
        System.out.println("Build Number  : " + buildNumber);
        System.out.println("Build Log Size: "
                + (buildLog != null ? buildLog.length() : 0)
                + " characters");
        System.out.println("================================================");

        // Extract only relevant failure/error information
        String relevantLogs =
                jenkinsLogProcessor.extractRelevantLogs(buildLog);

        System.out.println();
        System.out.println("================================================");
        System.out.println("       RELEVANT LOGS EXTRACTED");
        System.out.println("================================================");
        System.out.println("Relevant Log Size: "
                + relevantLogs.length()
                + " characters");
        System.out.println("================================================");

        // Send only relevant logs to the AI service
        return aiAnalysisService.analyze(relevantLogs);
    }
}