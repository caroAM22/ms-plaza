-- Table Category
IF NOT EXISTS CREATE TABLE category (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT
);

-- Table Restaurants
IF NOT EXISTS CREATE TABLE restaurants (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(200) NOT NULL,
    owner_id CHAR(36) NOT NULL, -- FK to users table (ms-users)
    phone VARCHAR(13) NOT NULL,
    logo_url VARCHAR(500) NOT NULL,
    nit INT NOT NULL UNIQUE
);

-- Table Dishes
IF NOT EXISTS CREATE TABLE dishes (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category_id CHAR(36) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    restaurant_id CHAR(36) NOT NULL,
    image_url VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (category_id) REFERENCES category(id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

-- Table Orders
IF NOT EXISTS CREATE TABLE orders (
    id CHAR(36) PRIMARY KEY,
    client_id CHAR(36) NOT NULL, -- FK to users table (ms-users)
    date DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    chef_id CHAR(36), -- FK to users table (ms-users)
    restaurant_id CHAR(36) NOT NULL,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

-- Table Orders_Dishes (relation table)
IF NOT EXISTS CREATE TABLE orders_dishes (
    order_id CHAR(36) NOT NULL,
    dish_id CHAR(36) NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY (order_id, dish_id),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (dish_id) REFERENCES dishes(id)
);