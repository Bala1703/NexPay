
package com.nexpay.agent.tools;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Component
public class GitInvestigationTool {

    private static final Path PROJECT_ROOT =
            Path.of("D:\\AI Powered CICD Optimization\\NexPay")
                    .toAbsolutePath()
                    .normalize();

    private static final int TIMEOUT_SECONDS = 10;
    private static final int MAX_OUTPUT_LENGTH = 12_000;

    public String inspectRecentChanges() {
        return runGitCommand("diff", "--no-ext-diff", "--unified=3", "HEAD~1", "HEAD");
    }

    public String inspectChangedFiles() {
        return runGitCommand("diff", "--name-only", "HEAD~1", "HEAD");
    }

    private String runGitCommand(String... arguments) {
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(buildCommand(arguments));
            builder.directory(PROJECT_ROOT.toFile());
            builder.redirectErrorStream(true);

            Process process = builder.start();

            boolean completed = process.waitFor(
                    TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (!completed) {
                process.destroyForcibly();
                return "Git investigation timed out.";
            }

            byte[] outputBytes = process.getInputStream().readNBytes(
                    MAX_OUTPUT_LENGTH + 1);

            String output = new String(
                    outputBytes, StandardCharsets.UTF_8);

            if (output.length() > MAX_OUTPUT_LENGTH) {
                output = output.substring(0, MAX_OUTPUT_LENGTH)
                        + "\n[Output truncated]";
            }

            if (process.exitValue() != 0) {
                return "Git command failed with exit code "
                        + process.exitValue() + ":\n" + output;
            }

            return output.isBlank()
                    ? "No matching Git changes found."
                    : output;

        } catch (IOException e) {
            return "Unable to execute Git investigation: "
                    + e.getMessage();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Git investigation was interrupted.";
        }
    }

    private String[] buildCommand(String... arguments) {
        String[] command = new String[arguments.length + 1];
        command[0] = "git";
        System.arraycopy(arguments, 0, command, 1, arguments.length);
        return command;
    }
}
