
package com.nexpay.ai_cicd_analyzer.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class JenkinsLogProcessor {

    private static final int CHUNK_SIZE = 4000;

    // Detect common credentials assigned using = or :
    private static final Pattern SECRET_PATTERN = Pattern.compile(
            "(?i)\\b(password|passwd|pwd|api[_-]?key"
                    + "|access[_-]?token|refresh[_-]?token"
                    + "|client[_-]?secret|secret[_-]?key)"
                    + "\\b(\\s*[:=]\\s*)(?:\"[^\"]*\"|'[^']*'|[^\\s,;]+)"
    );

    // Detect credentials in Authorization headers
    private static final Pattern AUTHORIZATION_PATTERN = Pattern.compile(
            "(?im)(\\bAuthorization\\s*:\\s*)[^\\r\\n]+"
    );

    // Detect Bearer tokens outside Authorization headers too
    private static final Pattern BEARER_PATTERN = Pattern.compile(
            "(?i)\\b(Bearer\\s+)[A-Za-z0-9._~+/=-]+"
    );

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

        String extractedLogs = String.join(
                System.lineSeparator(),
                relevantLogs
        );

        // Redact secrets before returning logs to the AI service
        return redactSensitiveData(extractedLogs);
    }

    private String redactSensitiveData(String text) {

        text = SECRET_PATTERN.matcher(text)
                .replaceAll("$1$2[REDACTED]");

        text = AUTHORIZATION_PATTERN.matcher(text)
                .replaceAll("$1[REDACTED]");

        text = BEARER_PATTERN.matcher(text)
                .replaceAll("$1[REDACTED]");

        return text;
    }

    public List<String> splitIntoChunks(String text) {

        List<String> chunks = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return chunks;
        }

        for (int start = 0; start < text.length(); start += CHUNK_SIZE) {

            int end = Math.min(start + CHUNK_SIZE, text.length());

            chunks.add(text.substring(start, end));
        }

        return chunks;
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
