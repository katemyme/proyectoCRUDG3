-- Script de la base de datos bdventas
-- Antes de ejecutarlo, crear la base de datos y conectarse a ella:
--   CREATE DATABASE bdventas;

CREATE SCHEMA catalogos;
CREATE TABLE catalogos.marcas
(
	id_marca SERIAL PRIMARY KEY,
	nombre VARCHAR(80) NOT NULL UNIQUE,
	pais_origen VARCHAR(80) NOT NULL,
	estado SMALLINT NOT NULL DEFAULT 1,

	CONSTRAINT chk_marca_estado
		CHECK (estado IN (0,1))
);

-- Pruebas para validar los CONSTRAINTS de Marcas
-- Están comentadas para que el script se ejecute completo:
-- la primera falla a propósito (nombre NULL) y las otras agregarían
-- marcas que no son del catálogo y cambiarían los id que usan los productos.
-- INSERT INTO catalogos.marcas(nombre,pais_origen,estado) VALUES (NULL, 'Nicaragua', 1);
-- INSERT INTO catalogos.marcas(nombre,pais_origen,estado) VALUES ('Almohada', 'Nicaragua', 1);
-- INSERT INTO catalogos.marcas(nombre,pais_origen) VALUES ('Audifonos', 'Nicaragua');

CREATE TABLE catalogos.categorias
(
	id_categorias SERIAL PRIMARY KEY,
	nombre VARCHAR(80) NOT NULL UNIQUE,
	descripcion VARCHAR(255),
	estado SMALLINT NOT NULL DEFAULT 1,

	CONSTRAINT chk_categoria_estado
		CHECK(estado IN (0,1))
);

-- Pruebas para validar los CONSTRAINTS de Categorias (Fallarán algunas por diseño)
-- Están comentadas por la misma razón que las de Marcas.
-- INSERT INTO catalogos.categorias(nombre,descripcion, estado) VALUES (NULL, 'lujo silencioso', 1);
-- INSERT INTO catalogos.categorias(nombre,descripcion, estado) VALUES ('joyeria', 'lujo silencioso', 1);
-- INSERT INTO catalogos.categorias(nombre,descripcion) VALUES ('billeteras', 'billeteras de lujo');


CREATE TABLE catalogos.productos
(
	id_productos SERIAL PRIMARY KEY,
	codigo VARCHAR(20) NOT NULL UNIQUE,
	nombre VARCHAR(120) NOT NULL UNIQUE,
	descripcion VARCHAR(120),
	precio NUMERIC(10,2) NOT NULL,
	stock INTEGER NOT NULL DEFAULT 0,
	fecha_creacion DATE NOT NULL DEFAULT CURRENT_DATE,
	id_categoria INTEGER NOT NULL,
	id_marca INTEGER NOT NULL,
	estado SMALLINT NOT NULL DEFAULT 1,

	CONSTRAINT fk_producto_categoria
		FOREIGN KEY(id_categoria)
		REFERENCES catalogos.categorias(id_categorias),

	CONSTRAINT fk_producto_marca
		FOREIGN KEY(id_marca)
		REFERENCES catalogos.marcas(id_marca),

	CONSTRAINT chk_producto_precio
		CHECK (precio > 0),

	CONSTRAINT chk_producto_stock
		CHECK (stock >= 0),

	CONSTRAINT chk_producto_estado
		CHECK (estado IN (0,1))
);

INSERT INTO catalogos.categorias(nombre, descripcion, estado) VALUES
('Laptops', 'Computadoras portátiles', 1),
('Periféricos', 'Mouses y teclados', 1),
('Monitores', 'Pantallas', 1),
('Mobiliario', 'Sillas y escritorios', 1),
('Cargadores', 'Cables y adaptadores', 1),
('Almacenamiento', 'Discos y SSD', 1),
('Webcams', 'Cámaras y audio', 1),
('Bolsos', 'Mochilas y fundas', 1);
SELECT * FROM catalogos.categorias;

INSERT INTO catalogos.marcas(nombre, pais_origen, estado) VALUES
('Dell', 'USA', 1),
('Logitech', 'Suiza', 1),
('Samsung', 'Corea', 1),
('ASUS', 'USA', 1),
('Kingston', 'USA', 1),
('HP', 'USA', 1);
SELECT * FROM catalogos.marcas;


INSERT INTO catalogos.productos (codigo, nombre, descripcion, precio, stock, id_categoria, id_marca)
VALUES
    ('PROD-010', 'Laptop XPS 15', 'Laptop de alto rendimiento', 1500.00, 10, 1, 1),
    ('PROD-011', 'Mouse Inalámbrico G', 'Mouse ergonómico', 25.50, 50, 2, 2),
    ('PROD-012', 'Teclado Mecánico K', 'Teclado retroiluminado', 85.00, 30, 2, 2),
    ('PROD-013', 'Monitor 4K 27"', 'Monitor para diseño gráfico', 350.00, 15, 3, 3),
    ('PROD-014', 'Auriculares Noise Cancelling', 'Cancelación de ruido activa', 120.00, 25, 7, 1),
    ('PROD-015', 'Silla Ergonómica Pro', 'Silla de oficina ajustable', 200.00, 12, 4, 4),
    ('PROD-016', 'Escritorio Ajustable', 'Escritorio motorizado', 450.00, 8, 4, 4),
    ('PROD-017', 'Cargador Rápido 65W', 'Cargador USB-C multipuerto', 35.00, 100, 5, 2),
    ('PROD-018', 'Cable USB-C a USB-C', 'Cable de carga rápida', 15.00, 200, 5, 2),
    ('PROD-019', 'Disco Duro Externo 2TB', 'Almacenamiento portátil HDD', 80.00, 40, 6, 5),
    ('PROD-020', 'SSD NVMe 1TB', 'Unidad de estado sólido rápida', 110.00, 35, 6, 5),
    ('PROD-021', 'Webcam HD 1080p', 'Cámara para videoconferencias', 45.00, 60, 7, 3),
    ('PROD-022', 'Micrófono de Condensador', 'Micrófono para podcasting', 95.00, 20, 7, 3),
    ('PROD-023', 'Funda para Laptop 15"', 'Funda resistente al agua', 20.00, 80, 8, 6),
    ('PROD-024', 'Mochila Antirrobo', 'Mochila con candado', 60.00, 45, 8, 6);
SELECT * FROM catalogos.productos;


select * FROM catalogos.productos
WHERE estado =1  AND stock >0;

SELECT * FROM catalogos.productos
WHERE precio >=50;

SELECT * FROM catalogos.productos
WHERE precio BETWEEN 20.00 AND 100.00;

SELECT * FROM catalogos.productos
WHERE precio>=20.00 AND precio<=100.00;


SELECT nombre
FROM catalogos.productos
WHERE nombre LIKE '%USB%';

Select *
FROM catalogos.productos
WHERE codigo ILIKE '%PRD%' or nombre ILIKE '%PRD%';


SELECT *
FROM catalogos.productos;

SELECT
	p.codigo,
    p.nombre ,
    c.nombre  AS Categoria,
    m.nombre AS Marca,
	p.precio,
	p.stock,
	p.fecha_creacion
FROM catalogos.productos AS p
INNER JOIN catalogos.categorias AS c ON p.id_categoria = c.id_categorias
INNER JOIN catalogos.marcas AS m ON p.id_marca = m.id_marca
WHERE p.estado = 1;
