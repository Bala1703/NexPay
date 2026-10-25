package com.nexpay.agent.model;

public record InvestigationRequest(
        String serviceName,
        String buildNumber,
        String buildLog
) {
}