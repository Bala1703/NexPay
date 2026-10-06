package com.nexpay.ai_cicd_analyzer.service;

import com.nexpay.ai_cicd_analyzer.model.AiAnalysisResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiAnalysisService {

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

        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .entity(AiAnalysisResponse.class);
    }
}