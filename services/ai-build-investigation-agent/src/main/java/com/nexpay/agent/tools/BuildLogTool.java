
package com.nexpay.agent.tools;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class BuildLogTool {

    private static final int MAX_LOG_LENGTH = 20_000;
    private static final int MAX_EVIDENCE_LINES = 25;

    private static final List<String> ERROR_MARKERS = List.of(
            "[ERROR]",
            "BUILD FAILURE",
            "COMPILATION ERROR",
            "FAILED",
            "Exception",
            "Caused by:",
            "Could not resolve",
            "Could not find artifact",
            "Tests run:",
            "AssertionError"
    );

    public List<String> extractEvidence(String buildLog) {
        if (buildLog == null || buildLog.isBlank()) {
            return List.of("No build log was provided.");
        }

        String boundedLog = buildLog.length() > MAX_LOG_LENGTH
                ? buildLog.substring(buildLog.length() - MAX_LOG_LENGTH)
                : buildLog;

        String[] lines = boundedLog.split("\\R");
        Set<String> evidence = new LinkedHashSet<>();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String normalized = line.toLowerCase(Locale.ROOT);

            boolean isRelevant = ERROR_MARKERS.stream()
                    .anyMatch(marker ->
                            normalized.contains(marker.toLowerCase(Locale.ROOT)));

            if (isRelevant) {
                evidence.add(line.trim());

                // Preserve a few surrounding lines for diagnostic context.
                if (i + 1 < lines.length
                        && !lines[i + 1].isBlank()) {
                    evidence.add(lines[i + 1].trim());
                }

                if (evidence.size() >= MAX_EVIDENCE_LINES) {
                    break;
                }
            }
        }

        if (evidence.isEmpty()) {
            evidence.add("No recognized failure markers found. "
                    + "The agent should inspect the available log context.");
        }

        return new ArrayList<>(evidence);
    }
}
