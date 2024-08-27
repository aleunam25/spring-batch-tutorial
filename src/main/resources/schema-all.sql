DROP TABLE products IF EXISTS;

CREATE TABLE products  (
    name VARCHAR(40),
    quantity INT,
    company VARCHAR(50),
    price FLOAT,
    section VARCHAR(50)
);