package com.nexpay.ai_cicd_analyzer.controller;

import com.nexpay.ai_cicd_analyzer.model.AiAnalysisResponse;
import com.nexpay.ai_cicd_analyzer.service.AiAnalysisService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analyze")
public class AiAnalysisController {

    private final AiAnalysisService aiAnalysisService;

    public AiAnalysisController(AiAnalysisService aiAnalysisService) {
        this.aiAnalysisService = aiAnalysisService;
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

        return aiAnalysisService.analyze(buildLog);
    }
}