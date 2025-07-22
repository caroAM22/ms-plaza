-- Tabla Categoria
CREATE TABLE IF NOT EXISTS category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

-- Table Restaurants
CREATE TABLE IF NOT EXISTS restaurants (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    address VARCHAR(200) NOT NULL,
    owner_id CHAR(36) NOT NULL,
    phone VARCHAR(13) NOT NULL,
    logo_url VARCHAR(500) NOT NULL,
    nit INT NOT NULL UNIQUE
);

-- Table Dishes
CREATE TABLE IF NOT EXISTS dishes (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    price INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    category_id INT NOT NULL,
    restaurant_id CHAR(36) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE 
); 

-- Table Orders
CREATE TABLE IF NOT EXISTS orders (
    id CHAR(36) PRIMARY KEY,
    client_id CHAR(36) NOT NULL,
    date DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    chef_id CHAR(36),
    restaurant_id CHAR(36) NOT NULL,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

-- Table Orders_Dishes (relation table)
CREATE TABLE IF NOT EXISTS orders_dishes (
    order_id CHAR(36) NOT NULL,
    dish_id CHAR(36) NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY (order_id, dish_id),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (dish_id) REFERENCES dishes(id)
);