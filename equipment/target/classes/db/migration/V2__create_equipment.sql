CREATE TABLE equipment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    serial_number VARCHAR(60) NOT NULL UNIQUE,
    brand VARCHAR(60) NOT NULL,
    model VARCHAR(60),
    client_id BIGINT NOT NULL,
    equipment_type_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    intake_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    CONSTRAINT fk_equipment_type FOREIGN KEY (equipment_type_id) REFERENCES equipment_types(id)
);
