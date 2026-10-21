
package com.nexpay.ai_cicd_analyzer.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JenkinsLogProcessorTest {

    private final JenkinsLogProcessor processor =
            new JenkinsLogProcessor();

    @Test
    void extractionShouldPreserveLargeRelevantLogs() {

        String buildLog = "START_ERROR\n"
                + "x".repeat(20000)
                + "\nEND_ERROR";

        String result = processor.extractRelevantLogs(buildLog);

        assertTrue(result.startsWith("START_ERROR"));
        assertTrue(result.contains("x".repeat(20000)));
        assertTrue(result.endsWith("END_ERROR"));
        assertTrue(result.length() > 12000);
    }

    @Test
    void splitIntoChunksShouldSplitLargeText() {

        String text = "A".repeat(9000);

        List<String> chunks = processor.splitIntoChunks(text);

        assertEquals(3, chunks.size());
        assertEquals(4000, chunks.get(0).length());
        assertEquals(4000, chunks.get(1).length());
        assertEquals(1000, chunks.get(2).length());
    }

    @Test
    void splitIntoChunksShouldReturnEmptyListForBlankText() {

        List<String> chunks = processor.splitIntoChunks("");

        assertTrue(chunks.isEmpty());
    }

    @Test
    void shouldRedactPasswordFromFailureLog() {

        String buildLog =
                "ERROR: password=MyDbPassword123";

        String result = processor.extractRelevantLogs(buildLog);

        assertTrue(result.contains("password=[REDACTED]"));
        assertFalse(result.contains("MyDbPassword123"));
    }

    @Test
    void shouldRedactApiKeyFromFailureLog() {

        String buildLog =
                "ERROR: api_key=sk_test_123456";

        String result = processor.extractRelevantLogs(buildLog);

        assertTrue(result.contains("api_key=[REDACTED]"));
        assertFalse(result.contains("sk_test_123456"));
    }

    @Test
    void shouldRedactAuthorizationHeader() {

        String buildLog =
                "ERROR: Authorization: Bearer abc123secret";

        String result = processor.extractRelevantLogs(buildLog);

        assertTrue(result.contains("Authorization: [REDACTED]"));
        assertFalse(result.contains("abc123secret"));
    }

    @Test
    void shouldRedactBearerToken() {

        String buildLog =
                "ERROR: Request failed with Bearer abc123secret";

        String result = processor.extractRelevantLogs(buildLog);

        assertTrue(result.contains("Bearer [REDACTED]"));
        assertFalse(result.contains("abc123secret"));
    }

    @Test
    void shouldPreserveUsefulErrorDetailsAfterRedaction() {

        String buildLog =
                "ERROR: Payment service failed; password=secret123";

        String result = processor.extractRelevantLogs(buildLog);

        assertTrue(result.contains("Payment service failed"));
        assertTrue(result.contains("password=[REDACTED]"));
        assertFalse(result.contains("secret123"));
    }
}
