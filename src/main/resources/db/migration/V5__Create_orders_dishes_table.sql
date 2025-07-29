CREATE TABLE IF NOT EXISTS orders_dishes (
    order_id CHAR(36) NOT NULL,
    dish_id CHAR(36) NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY (order_id, dish_id),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (dish_id) REFERENCES dishes(id)
); 