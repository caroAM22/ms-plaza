CREATE TABLE IF NOT EXISTS dishes (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    category_id INT NOT NULL,
    restaurant_id CHAR(36) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE (name, restaurant_id),
    FOREIGN KEY (category_id) REFERENCES category(id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
); 