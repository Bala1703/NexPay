package com.nexpay.ai_cicd_analyzer.service;

import com.nexpay.ai_cicd_analyzer.model.AiAnalysisResponse;
import org.springframework.stereotype.Service;

@Service
public class AiAnalysisService {

    public AiAnalysisResponse analyze(String buildLog) {

        AiAnalysisResponse response = new AiAnalysisResponse();

        response.setFailureType("UNKNOWN");
        response.setRootCause("AI analysis is not connected yet.");
        response.setRecommendation("LLM integration will be added in the next step.");
        response.setSeverity("UNKNOWN");

        return response;
    }
}