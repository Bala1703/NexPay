package com.nexpay.ai_cicd_analyzer.controller;

import com.nexpay.ai_cicd_analyzer.model.AiAnalysisResponse;
import com.nexpay.ai_cicd_analyzer.service.AiAnalysisService;
import com.nexpay.ai_cicd_analyzer.service.JenkinsLogProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/analyze")
public class AiAnalysisController {

    private static final int MAX_LOG_SIZE = 1_000_000;
    private static final int MAX_SERVICE_NAME_LENGTH = 100;

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

        // Validate the service name
        if (serviceName == null || serviceName.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Service name must not be blank"
            );
        }

        if (serviceName.length() > MAX_SERVICE_NAME_LENGTH) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Service name is too long"
            );
        }

        // Allow only ordinary service-name characters
        if (!serviceName.matches("[a-zA-Z0-9._-]+")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Service name contains invalid characters"
            );
        }

        // Validate the build number
        if (buildNumber <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Build number must be positive"
            );
        }

        // Validate the build log
        if (buildLog == null || buildLog.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Build log must not be blank"
            );
        }

        if (buildLog.length() > MAX_LOG_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "Build log exceeds the allowed size"
            );
        }

        // Log metadata only after validation
        System.out.println();
        System.out.println("================================================");
        System.out.println("       AI CI/CD ANALYZER - LOG RECEIVED");
        System.out.println("================================================");
        System.out.println("Service Name  : " + serviceName);
        System.out.println("Build Number  : " + buildNumber);
        System.out.println("Build Log Size: " + buildLog.length()
                + " characters");
        System.out.println("================================================");

        // Extract relevant logs and redact recognized secrets
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

        // Reject logs that contain no recognizable relevant build errors
        if (relevantLogs == null || relevantLogs.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No relevant Jenkins build errors were found in the supplied log."
            );
        }

        // Send only processed logs to the AI service
        return aiAnalysisService.analyze(relevantLogs);
    }
}
