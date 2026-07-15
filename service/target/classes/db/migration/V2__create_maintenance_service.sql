CREATE TABLE maintenance_services (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    price DECIMAL(10,2) NOT NULL,
    category_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_service_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT uk_service_name_category UNIQUE (name, category_id)
);
