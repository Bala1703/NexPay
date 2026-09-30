package com.nexpay.ai_cicd_analyzer.service;

import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class JenkinsLogProcessor {

    private static final int MAX_LOG_LENGTH = 12000;

    public String extractRelevantLogs(String buildLog) {

        if (buildLog == null || buildLog.isBlank()) {
            return "";
        }

        String[] lines = buildLog.split("\\R");

        Set<String> relevantLogs = new LinkedHashSet<>();

        for (int i = 0; i < lines.length; i++) {

            String line = lines[i];

            if (isFailureLine(line)) {

                int start = Math.max(0, i - 3);

                int end = i;

                // Capture following stack-trace/error lines
                while (end + 1 < lines.length
                        && isRelatedFailureLine(lines[end + 1])) {

                    end++;

                    // Safety limit for one failure block
                    if (end - i >= 30) {
                        break;
                    }
                }

                for (int j = start; j <= end; j++) {
                    relevantLogs.add(lines[j]);
                }
            }
        }

        String result = String.join(System.lineSeparator(), relevantLogs);

        // Prevent extremely large input from reaching the LLM
        if (result.length() > MAX_LOG_LENGTH) {
            result = result.substring(0, MAX_LOG_LENGTH)
                    + System.lineSeparator()
                    + "[LOG TRUNCATED]";
        }

        return result;
    }

    private boolean isFailureLine(String line) {

        return line.contains("ERROR")
                || line.contains("Exception")
                || line.contains("FAILED")
                || line.contains("FAILURE");
    }

    private boolean isRelatedFailureLine(String line) {

        String trimmedLine = line.trim();

        return trimmedLine.startsWith("at ")
                || trimmedLine.startsWith("Caused by:")
                || trimmedLine.contains("Exception")
                || trimmedLine.contains("ERROR")
                || trimmedLine.contains("FAILED")
                || trimmedLine.contains("FAILURE")
                || trimmedLine.startsWith("[ERROR]");
    }
}