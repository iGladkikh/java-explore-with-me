CREATE TABLE IF NOT EXISTS requests
(
    id         BIGINT NOT NULL,
    app        VARCHAR(127),
    uri        VARCHAR(255),
    ip         VARCHAR(45),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_request PRIMARY KEY (id)
);