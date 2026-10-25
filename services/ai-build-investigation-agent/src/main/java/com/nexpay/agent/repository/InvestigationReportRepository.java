package com.nexpay.agent.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexpay.agent.model.InvestigationReport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class InvestigationReportRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public InvestigationReportRepository(
            JdbcTemplate jdbcTemplate,
            ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public void save(InvestigationReport report) {
        try {
            String reportJson = objectMapper.writeValueAsString(report);

            String sql = """
                    INSERT INTO investigation_reports (
                        service_name,
                        build_number,
                        status,
                        root_cause,
                        confidence,
                        report_json
                    )
                    VALUES (?, ?, ?, ?, ?, CAST(? AS jsonb))
                    """;

            jdbcTemplate.update(
                    sql,
                    report.serviceName(),
                    report.buildNumber(),
                    report.status(),
                    report.rootCause(),
                    report.confidence(),
                    reportJson
            );

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Failed to convert investigation report to JSON.", e);
        }
    }
}