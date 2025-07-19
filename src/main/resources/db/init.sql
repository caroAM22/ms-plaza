-- Tabla Categoria
IF NOT EXISTS CREATE TABLE categoria (
    id CHAR(36) PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion TEXT
);

-- Tabla Restaurantes
IF NOT EXISTS CREATE TABLE restaurantes (
    id CHAR(36) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    id_propietario CHAR(36) NOT NULL, -- FK a tabla users (ms-users)
    telefono VARCHAR(13) NOT NULL,
    url_logo VARCHAR(500) NOT NULL,
    nit VARCHAR(20) NOT NULL UNIQUE
);

-- Tabla Platos
IF NOT EXISTS CREATE TABLE platos (
    id CHAR(36) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    id_categoria CHAR(36) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2) NOT NULL,
    id_restaurante CHAR(36) NOT NULL,
    url_imagen VARCHAR(500),
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id),
    FOREIGN KEY (id_restaurante) REFERENCES restaurantes(id)
);

-- Tabla Pedidos
IF NOT EXISTS CREATE TABLE pedidos (
    id CHAR(36) PRIMARY KEY,
    id_cliente CHAR(36) NOT NULL, -- FK a tabla users (ms-users)
    fecha DATETIME NOT NULL,
    estado VARCHAR(50) NOT NULL,
    id_chef CHAR(36), -- FK a tabla users (ms-users)
    id_restaurante CHAR(36) NOT NULL,
    FOREIGN KEY (id_restaurante) REFERENCES restaurantes(id)
);

-- Tabla Pedidos_Platos (tabla de relación)
IF NOT EXISTS CREATE TABLE pedidos_platos (
    id_pedido CHAR(36) NOT NULL,
    id_plato CHAR(36) NOT NULL,
    cantidad INT NOT NULL,
    PRIMARY KEY (id_pedido, id_plato),
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id),
    FOREIGN KEY (id_plato) REFERENCES platos(id)
);