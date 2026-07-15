-- Version de prueba (H2): igual a la de produccion pero sin CREATE EXTENSION pgcrypto,
-- que H2 no soporta. Los tests insertan usuarios directamente vía el repositorio.
CREATE TABLE IF NOT EXISTS users (
    email VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);
