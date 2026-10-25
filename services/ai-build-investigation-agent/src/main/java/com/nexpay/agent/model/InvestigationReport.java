package com.nexpay.agent.model;

import java.util.List;

public record InvestigationReport(
        String serviceName,
        String buildNumber,
        String status,
        String rootCause,
        String confidence,
        List<String> evidence,
        List<String> actionsTaken,
        String recommendation
) {
}