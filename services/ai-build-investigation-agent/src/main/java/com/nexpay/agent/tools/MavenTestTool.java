
package com.nexpay.agent.tools;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class MavenTestTool {

    private static final Path PROJECT_ROOT =
            Path.of("D:\\AI Powered CICD Optimization\\NexPay")
                    .toAbsolutePath()
                    .normalize();

    private static final Path MAVEN_WRAPPER =
            PROJECT_ROOT.resolve("services")
                    .resolve("ai-cicd-analyzer")
                    .resolve("mvnw.cmd");

    private static final int TIMEOUT_SECONDS = 90;
    private static final int MAX_OUTPUT_LENGTH = 12_000;

    private static final Map<String, String> ALLOWED_MODULES = Map.of(
            "user-service", "services/user-service/pom.xml",
            "bank-account-service", "services/bank-account-service/pom.xml",
            "nexpay-connection-service", "services/nexpay-connection-service/pom.xml",
            "nexpay-payment-service", "services/nexpay-payment-service/pom.xml",
            "nexpay-transaction-service", "services/nexpay-transaction-service/pom.xml"
    );

    public String runTargetedTest(String serviceName) {

        String pomRelativePath = ALLOWED_MODULES.get(serviceName);

        if (pomRelativePath == null) {
            return "Test not permitted. The requested service is not allowlisted.";
        }

        Path pomPath = PROJECT_ROOT.resolve(pomRelativePath).normalize();

        if (!pomPath.startsWith(PROJECT_ROOT)
                || !Files.isRegularFile(pomPath)) {
            return "Cannot run test: the service POM was not found.";
        }

        if (!Files.isRegularFile(MAVEN_WRAPPER)) {
            return "Cannot run test: Maven Wrapper was not found.";
        }

        Path outputFile = null;
        Process process = null;

        try {
            outputFile = Files.createTempFile(
                    "nexpay-agent-maven-", ".log");

            String command = "\"" + MAVEN_WRAPPER + "\""
                    + " -f \"" + pomPath + "\" test";

            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/d", "/s", "/c",
                    "\"" + command + "\""
            );

            builder.directory(PROJECT_ROOT.toFile());
            builder.redirectErrorStream(true);
            builder.redirectOutput(outputFile.toFile());

            process = builder.start();

            boolean completed = process.waitFor(
                    TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (!completed) {
                process.destroyForcibly();
                process.waitFor(5, TimeUnit.SECONDS);

                return "Maven test timed out after "
                        + TIMEOUT_SECONDS + " seconds. "
                        + "The test result is inconclusive.";
            }

            String output = Files.readString(
                    outputFile, StandardCharsets.UTF_8);

            if (output.length() > MAX_OUTPUT_LENGTH) {
                output = output.substring(
                        output.length() - MAX_OUTPUT_LENGTH);
                output = "[Earlier output omitted]\n" + output;
            }

            if (process.exitValue() == 0) {
                return "TESTS PASSED for " + serviceName
                        + "\n" + output;
            }

            return "TESTS FAILED for " + serviceName
                    + ". Exit code: " + process.exitValue()
                    + "\n" + output;

        } catch (IOException e) {
            return "Unable to execute Maven test: "
                    + e.getMessage();

        } catch (InterruptedException e) {
            if (process != null) {
                process.destroyForcibly();
            }

            Thread.currentThread().interrupt();
            return "Maven test was interrupted.";

        } finally {
            if (outputFile != null) {
                try {
                    Files.deleteIfExists(outputFile);
                } catch (IOException ignored) {
                    // Temporary log cleanup is best-effort.
                }
            }
        }
    }
}
