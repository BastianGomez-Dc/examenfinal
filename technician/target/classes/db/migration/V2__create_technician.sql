CREATE TABLE technicians (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    phone VARCHAR(20),
    specialty_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    hire_date DATE NOT NULL DEFAULT CURRENT_DATE,
    CONSTRAINT fk_technician_specialty FOREIGN KEY (specialty_id) REFERENCES specialties(id)
);
