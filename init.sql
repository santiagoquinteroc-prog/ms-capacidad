CREATE TABLE IF NOT EXISTS capacidad (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion VARCHAR(90)
);

CREATE TABLE IF NOT EXISTS capacidad_tecnologia (
    capacidad_id BIGINT NOT NULL,
    tecnologia_id BIGINT NOT NULL,
    PRIMARY KEY (capacidad_id, tecnologia_id),
    FOREIGN KEY (capacidad_id) REFERENCES capacidad(id)
);

