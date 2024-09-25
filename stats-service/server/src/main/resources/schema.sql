CREATE TABLE IF NOT EXISTS request
(
    id        BIGINT NOT NULL,
    app       VARCHAR(127),
    uri       VARCHAR(255),
    ip        VARCHAR(45),
    timestamp TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_request PRIMARY KEY (id)
);