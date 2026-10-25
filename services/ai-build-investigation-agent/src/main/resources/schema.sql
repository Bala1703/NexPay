CREATE TABLE IF NOT EXISTS investigation_reports (
    id BIGSERIAL PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    build_number VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL,
    root_cause TEXT,
    confidence VARCHAR(20),
    report_json JSONB NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);