CREATE TABLE citas (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    tecnico_id BIGINT NOT NULL,
    fecha_hora TIMESTAMP NOT NULL,
    motivo VARCHAR(200),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);
