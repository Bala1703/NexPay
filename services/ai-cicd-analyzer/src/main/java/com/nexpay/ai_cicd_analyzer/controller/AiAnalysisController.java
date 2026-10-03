package com.nexpay.ai_cicd_analyzer.controller;

import com.nexpay.ai_cicd_analyzer.dto.BuildLogRequest;
import com.nexpay.ai_cicd_analyzer.model.AiAnalysisResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analyze")
public class AiAnalysisController {

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
        System.out.println("       REQUEST RECEIVED SUCCESSFULLY");
        System.out.println("================================================");
        System.out.println();

        return null;
    }
}
