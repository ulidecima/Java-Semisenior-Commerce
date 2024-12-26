-- Crear la base de datos si no existe
CREATE DATABASE javassrcommercedb;

-- Seleccionar la base de datos para su uso
\c javassrcommercedb;

-- Crear la tabla usuarios
CREATE TABLE usuarios (
                          id SERIAL PRIMARY KEY,
                          nombre VARCHAR(255) NOT NULL,
                          email VARCHAR(255) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL,
                          habilitado BOOLEAN NOT NULL DEFAULT TRUE
);

-- Crear la tabla productos
CREATE TABLE productos (
                           id SERIAL PRIMARY KEY,
                           nombre VARCHAR(255) NOT NULL,
                           descripcion TEXT NOT NULL,
                           precio DECIMAL(10, 2) NOT NULL,
                           stockDisponible INT NOT NULL
);

-- Crear la tabla pedidos
CREATE TABLE pedidos (
                         id SERIAL PRIMARY KEY,
                         usuario_id INT NOT NULL,
                         precio DECIMAL(10, 2) NOT NULL,
                         fecha_de_creacion TIMESTAMP NOT NULL,
                         FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Crear la tabla detalles
CREATE TABLE detalles (
                          id SERIAL PRIMARY KEY,
                          producto_id INT NOT NULL,
                          pedido_id INT NOT NULL,
                          cantidad INT NOT NULL,
                          FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE,
                          FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE
);

-- Insertar datos de ejemplo

-- Insertar productos
INSERT INTO productos (nombre, descripcion, precio, stockDisponible)
VALUES ('Remera', 'Remera de algodón', 15.50, 100),
       ('Pantalon', 'Pantalon de jean', 30.00, 50);

