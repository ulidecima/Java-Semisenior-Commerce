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

-- Insertar productos
INSERT INTO productos (precio, stockDisponible, descripcion, nombre)
VALUES (15.50, 100, 'Remera de algodón', 'Remera'),
       (30.00, 50, 'Pantalon de jean', 'Pantalon'),
       (100.0, 100, 'Camisetas de algodon color verde', 'Camiseta'),
       (220.5, 90, 'Camisas de lino color verde', 'Camisa'),
       (2040.5, 90, 'Camisas de lino color naranja', 'Camisa'),
       (1200.5, 90, 'Camisas de lino color amarillo', 'Camisa'),
       (32400.5, 90, 'Camisas de lino color rosa', 'Camisa'),
       (10.5, 90, 'Camisas de lino color azul', 'Camisa'),
       (2.5, 90, 'Camisas de lino color violeta', 'Camisa');

