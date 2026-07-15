-- Table name is double-quoted because ORDER is a reserved SQL keyword in Postgres.
CREATE TABLE "order" (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    equipment_id BIGINT NOT NULL,
    technician_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'RECEIVED',
    total NUMERIC(10,2) NOT NULL DEFAULT 0,
    created_date DATE NOT NULL DEFAULT CURRENT_DATE
);
