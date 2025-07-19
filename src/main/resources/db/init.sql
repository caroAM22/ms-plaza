-- Tabla Categoria
IF NOT EXISTS CREATE TABLE categoria (
    id VARCHAR(36) PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion TEXT
);

-- Tabla Restaurantes
IF NOT EXISTS CREATE TABLE restaurantes (
    id VARCHAR(36) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    id_propietario VARCHAR(36) NOT NULL, -- FK a tabla users (ms-users)
    telefono VARCHAR(20) NOT NULL,
    url_logo VARCHAR(500),
    nit VARCHAR(20) NOT NULL UNIQUE
);

-- Tabla Platos
IF NOT EXISTS CREATE TABLE platos (
    id VARCHAR(36) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    id_categoria VARCHAR(36) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2) NOT NULL,
    id_restaurante VARCHAR(36) NOT NULL,
    url_imagen VARCHAR(500),
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id),
    FOREIGN KEY (id_restaurante) REFERENCES restaurantes(id)
);

-- Tabla Pedidos
IF NOT EXISTS CREATE TABLE pedidos (
    id VARCHAR(36) PRIMARY KEY,
    id_cliente VARCHAR(36) NOT NULL, -- FK a tabla users (ms-users)
    fecha DATETIME NOT NULL,
    estado VARCHAR(50) NOT NULL,
    id_chef VARCHAR(36), -- FK a tabla users (ms-users)
    id_restaurante VARCHAR(36) NOT NULL,
    FOREIGN KEY (id_restaurante) REFERENCES restaurantes(id)
);

-- Tabla Pedidos_Platos (tabla de relación)
IF NOT EXISTS CREATE TABLE pedidos_platos (
    id_pedido VARCHAR(36) NOT NULL,
    id_plato VARCHAR(36) NOT NULL,
    cantidad INT NOT NULL,
    PRIMARY KEY (id_pedido, id_plato),
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id),
    FOREIGN KEY (id_plato) REFERENCES platos(id)
);