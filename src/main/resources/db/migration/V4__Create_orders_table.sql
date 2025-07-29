CREATE TABLE IF NOT EXISTS orders (
    id CHAR(36) PRIMARY KEY,
    client_id CHAR(36) NOT NULL,
    date DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    chef_id CHAR(36),
    restaurant_id CHAR(36) NOT NULL,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
); 