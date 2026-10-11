-- V8: Transactional Outbox Table and Performance Indexes
-- Ensures atomic persistence of domain events with business entities, preventing dual-write hazards.
CREATE TABLE domain_event_outbox (
    id RAW(16) NOT NULL PRIMARY KEY,
    aggregate_type VARCHAR2(255) NOT NULL,
    aggregate_id VARCHAR2(255) NOT NULL,
    event_type VARCHAR2(500) NOT NULL,
    payload CLOB NOT NULL,
    occurred_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    retry_count NUMBER(10, 0) DEFAULT 0 NOT NULL,
    last_error CLOB
);

CREATE INDEX idx_outbox_unprocessed ON domain_event_outbox (processed_at, retry_count, occurred_at);
