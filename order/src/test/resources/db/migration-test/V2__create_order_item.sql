CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    service_name VARCHAR(100) NOT NULL,
    unit_price NUMERIC(10,2) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_order_item FOREIGN KEY (order_id) REFERENCES "order"(id)
);
