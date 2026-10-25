package com.nexpay.ai_cicd_analyzer.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class JenkinsLogProcessor {

    // Keep extracted logs small enough for a single analyzer request.
    private static final int MAX_EXTRACTED_LOG_CHARS = 12000;

    // The extracted log should normally fit into one chunk.
    private static final int CHUNK_SIZE = 12000;

    private static final int CONTEXT_LINES_BEFORE = 3;
    private static final int MAX_RELATED_LINES_AFTER = 30;

    private static final Pattern SECRET_PATTERN = Pattern.compile(
            "(?i)\\b(password|passwd|pwd|api[_-]?key"
                    + "|access[_-]?token|refresh[_-]?token"
                    + "|client[_-]?secret|secret[_-]?key)"
                    + "\\b(\\s*[:=]\\s*)(?:\"[^\"]*\"|'[^']*'|[^\\s,;]+)"
    );

    private static final Pattern AUTHORIZATION_PATTERN = Pattern.compile(
            "(?im)(\\bAuthorization\\s*:\\s*)[^\\r\\n]+"
    );

    private static final Pattern BEARER_PATTERN = Pattern.compile(
            "(?i)\\b(Bearer\\s+)[A-Za-z0-9._~+/=-]+"
    );

    private static final Pattern HTTP_ERROR_PATTERN = Pattern.compile(
            "(?i)\\bHTTP\\s*(?:status\\s*)?5\\d{2}\\b"
                    + "|\\bstatus\\s*[=: ]\\s*5\\d{2}\\b"
    );

    private static final Pattern TEST_FAILURE_PATTERN = Pattern.compile(
            "(?i)\\b(Tests run:.*\\bFailures:\\s*[1-9]\\d*"
                    + "|\\d+\\s+tests? failed"
                    + "|there are test failures"
                    + "|assertionerror"
                    + "|comparisonfailure)\\b"
    );

    public String extractRelevantLogs(String buildLog) {

        if (buildLog == null || buildLog.isBlank()) {
            return "";
        }

        String[] lines = buildLog.split("\\R", -1);

        List<FailureBlock> blocks = new ArrayList<>();

        // Identify candidate failure blocks.
        for (int i = 0; i < lines.length; i++) {

            if (!isFailureLine(lines[i])) {
                continue;
            }

            int start = Math.max(0, i - CONTEXT_LINES_BEFORE);
            int end = i;
            int relatedLines = 0;

            while (end + 1 < lines.length
                    && isRelatedFailureLine(lines[end + 1])
                    && relatedLines < MAX_RELATED_LINES_AFTER) {

                end++;
                relatedLines++;
            }

            blocks.add(new FailureBlock(start, end));
        }

        if (blocks.isEmpty()) {
            return "";
        }

        // Merge overlapping blocks so the same lines aren't repeatedly added.
        List<FailureBlock> mergedBlocks = mergeOverlappingBlocks(blocks);

        // Prefer blocks with stronger technical evidence.
        mergedBlocks.sort(
                Comparator.comparingInt(
                        (FailureBlock block) ->
                                scoreBlock(lines, block)
                ).reversed()
        );

        Set<Integer> selectedLineNumbers = new LinkedHashSet<>();
        int selectedCharacters = 0;

        for (FailureBlock block : mergedBlocks) {

            int blockCharacters = calculateBlockLength(lines, block);

            if (selectedCharacters + blockCharacters
                    <= MAX_EXTRACTED_LOG_CHARS) {

                addBlockLines(
                        selectedLineNumbers,
                        block
                );

                selectedCharacters += blockCharacters;

                continue;
            }

            // Don't let a large block prevent smaller, useful blocks
            // from being considered.
            if (selectedCharacters == 0) {

                addBlockLinesWithinLimit(
                        lines,
                        selectedLineNumbers,
                        block,
                        MAX_EXTRACTED_LOG_CHARS
                );
            }

            // Continue: another smaller, high-value block may fit.
        }

        // Restore original log order after selecting the best evidence.
        List<Integer> orderedLineNumbers =
                new ArrayList<>(selectedLineNumbers);

        orderedLineNumbers.sort(Integer::compareTo);

        List<String> selectedLines = new ArrayList<>();

        for (int lineNumber : orderedLineNumbers) {
            selectedLines.add(lines[lineNumber]);
        }

        String extractedLogs = String.join(
                System.lineSeparator(),
                selectedLines
        );

        // Redact sensitive data before returning logs to the AI service.
        return redactSensitiveData(extractedLogs);
    }

    private List<FailureBlock> mergeOverlappingBlocks(
            List<FailureBlock> blocks) {

        List<FailureBlock> sortedBlocks = new ArrayList<>(blocks);

        sortedBlocks.sort(
                Comparator.comparingInt(block -> block.start)
        );

        List<FailureBlock> merged = new ArrayList<>();

        for (FailureBlock current : sortedBlocks) {

            if (merged.isEmpty()) {
                merged.add(new FailureBlock(
                        current.start,
                        current.end
                ));
                continue;
            }

            FailureBlock previous = merged.get(merged.size() - 1);

            if (current.start <= previous.end + 1) {

                previous.end = Math.max(
                        previous.end,
                        current.end
                );

            } else {

                merged.add(new FailureBlock(
                        current.start,
                        current.end
                ));
            }
        }

        return merged;
    }

    private int scoreBlock(
            String[] lines,
            FailureBlock block) {

        int score = 0;

        for (int i = block.start; i <= block.end; i++) {

            String line = lines[i];
            String normalized = line.toLowerCase(Locale.ROOT);

            if (normalized.contains("caused by:")) {
                score += 12;
            }

            if (normalized.contains("build failure")) {
                score += 12;
            }

            if (normalized.contains("outofmemoryerror")
                    || normalized.contains("stackoverflowerror")) {
                score += 12;
            }

            if (HTTP_ERROR_PATTERN.matcher(line).find()) {
                score += 10;
            }

            if (TEST_FAILURE_PATTERN.matcher(line).find()) {
                score += 10;
            }

            if (normalized.contains("exception")) {
                score += 6;
            }

            if (normalized.contains("error")) {
                score += 5;
            }

            if (normalized.contains("failed")
                    || normalized.contains("failure")) {
                score += 5;
            }

            if (normalized.contains("timed out")
                    || normalized.contains("timeout")) {
                score += 4;
            }

            if (normalized.contains("connection refused")
                    || normalized.contains("connection reset")) {
                score += 4;
            }

            if (normalized.contains("compilation failure")) {
                score += 8;
            }

            if (normalized.contains("no tests were executed")) {
                score += 6;
            }
        }

        // Prefer shorter blocks when evidence scores are comparable.
        score -= (block.end - block.start) / 10;

        return score;
    }

    private int calculateBlockLength(
            String[] lines,
            FailureBlock block) {

        int length = 0;

        for (int i = block.start; i <= block.end; i++) {
            length += lines[i].length() + System.lineSeparator().length();
        }

        return length;
    }

    private void addBlockLines(
            Set<Integer> selectedLines,
            FailureBlock block) {

        for (int i = block.start; i <= block.end; i++) {
            selectedLines.add(i);
        }
    }

    private void addBlockLinesWithinLimit(
            String[] lines,
            Set<Integer> selectedLines,
            FailureBlock block,
            int characterLimit) {

        int characters = 0;

        for (int i = block.start; i <= block.end; i++) {

            int lineLength =
                    lines[i].length() + System.lineSeparator().length();

            if (characters + lineLength > characterLimit) {
                break;
            }

            selectedLines.add(i);
            characters += lineLength;
        }
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

            int end = Math.min(
                    start + CHUNK_SIZE,
                    text.length()
            );

            chunks.add(text.substring(start, end));
        }

        return chunks;
    }

    private boolean isFailureLine(String line) {

        String normalized = line.toLowerCase(Locale.ROOT);

        return normalized.contains("error")
                || normalized.contains("exception")
                || normalized.contains("failed")
                || normalized.contains("failure")
                || normalized.contains("caused by:")
                || normalized.contains("timed out")
                || normalized.contains("timeout")
                || normalized.contains("connection refused")
                || normalized.contains("connection reset")
                || HTTP_ERROR_PATTERN.matcher(line).find()
                || TEST_FAILURE_PATTERN.matcher(line).find();
    }

    private boolean isRelatedFailureLine(String line) {

        String trimmedLine = line.trim();
        String normalized = trimmedLine.toLowerCase(Locale.ROOT);

        return trimmedLine.startsWith("at ")
                || normalized.startsWith("caused by:")
                || normalized.startsWith("suppressed:")
                || normalized.contains("exception")
                || normalized.contains("error")
                || normalized.contains("failed")
                || normalized.contains("failure")
                || normalized.contains("timed out")
                || normalized.contains("timeout")
                || normalized.contains("connection refused")
                || normalized.contains("connection reset")
                || HTTP_ERROR_PATTERN.matcher(line).find()
                || TEST_FAILURE_PATTERN.matcher(line).find();
    }

    private static class FailureBlock {

        private final int start;
        private int end;

        private FailureBlock(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
}