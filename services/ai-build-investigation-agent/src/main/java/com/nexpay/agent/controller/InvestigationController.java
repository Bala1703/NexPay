package com.nexpay.agent.controller;

import com.nexpay.agent.model.InvestigationReport;
import com.nexpay.agent.model.InvestigationRequest;
import com.nexpay.agent.service.InvestigationAgentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/agent")
public class InvestigationController {

    private final InvestigationAgentService investigationAgentService;

    public InvestigationController(
            InvestigationAgentService investigationAgentService) {
        this.investigationAgentService = investigationAgentService;
    }

    @PostMapping("/investigate")
    public InvestigationReport investigate(
            @RequestBody InvestigationRequest request) {

        if (request == null
                || request.serviceName() == null
                || request.serviceName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "serviceName is required.");
        }

        if (request.buildNumber() == null
                || request.buildNumber().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "buildNumber is required.");
        }

        if (request.buildLog() == null
                || request.buildLog().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "buildLog is required.");
        }

        if (request.buildLog().length() > 200_000) {
            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "Build log must not exceed 200,000 characters.");
        }

        return investigationAgentService.investigate(request);
    }
}
