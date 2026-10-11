
package com.nexpay.ai_cicd_analyzer.service;

import com.nexpay.ai_cicd_analyzer.model.AiAnalysisResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

@Service
public class AiAnalysisService {

    private static final Logger logger =
            LoggerFactory.getLogger(AiAnalysisService.class);

    private static final Set<String> ALLOWED_SEVERITIES =
            Set.of("LOW", "MEDIUM", "HIGH", "CRITICAL");

    private final ChatClient chatClient;

    public AiAnalysisService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public AiAnalysisResponse analyze(String relevantLogs) {

        String prompt = """
                Analyze the following Jenkins CI/CD build failure.

                Return ONLY a valid JSON object.
                Do not include markdown.
                Do not include ```json.
                Do not include any explanation outside the JSON.

                The JSON must contain exactly these fields:
                {
                  "failureType": "type of failure",
                  "rootCause": "actual root cause",
                  "recommendation": "recommended action",
                  "severity": "LOW, MEDIUM, HIGH, or CRITICAL"
                }

                Jenkins relevant failure logs:
                %s
                """.formatted(relevantLogs);

        try {
            AiAnalysisResponse response = chatClient
                    .prompt()
                    .user(prompt)
                    .call()
                    .entity(AiAnalysisResponse.class);

            return validateResponse(response);

        } catch (RuntimeException exception) {

            logger.error(
                    "AI analysis failed or returned an invalid response.",
                    exception
            );

            return createFallbackResponse();
        }
    }

    private AiAnalysisResponse validateResponse(
            AiAnalysisResponse response) {

        if (response == null) {
            throw new IllegalStateException(
                    "AI returned an empty response."
            );
        }

        if (isBlank(response.getFailureType())) {
            throw new IllegalStateException(
                    "AI response has a missing failureType."
            );
        }

        if (isBlank(response.getRootCause())) {
            throw new IllegalStateException(
                    "AI response has a missing rootCause."
            );
        }

        if (isBlank(response.getRecommendation())) {
            throw new IllegalStateException(
                    "AI response has a missing recommendation."
            );
        }

        if (isBlank(response.getSeverity())) {
            throw new IllegalStateException(
                    "AI response has a missing severity."
            );
        }

        String severity = response.getSeverity()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!ALLOWED_SEVERITIES.contains(severity)) {
            throw new IllegalStateException(
                    "AI returned an unsupported severity: " + severity
            );
        }

        response.setSeverity(severity);

        return response;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private AiAnalysisResponse createFallbackResponse() {

        AiAnalysisResponse response = new AiAnalysisResponse();

        response.setFailureType("AI_ANALYSIS_UNAVAILABLE");

        response.setRootCause(
                "The AI analyzer could not complete the analysis. "
                + "The actual Jenkins build failure remains undetermined."
        );

        response.setRecommendation(
                "Check whether Ollama is running and the configured model "
                + "is available. Review the analyzer logs, then retry "
                + "the analysis."
        );

        // Placeholder severity for the analyzer failure,
        // not the original Jenkins build failure.
        response.setSeverity("MEDIUM");

        return response;
    }
}
