CREATE TABLE IF NOT EXISTS Orders (
    order_id    VARCHAR(50) PRIMARY KEY,
    merchant_id VARCHAR(50) NOT NULL,
    order_date  DATE        NOT NULL,
    status      VARCHAR(30) NOT NULL,
    gross_total DOUBLE      NOT NULL,
    discount    DOUBLE      NOT NULL,
    final_total DOUBLE      NOT NULL
);

CREATE TABLE IF NOT EXISTS Order_Items (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    order_id   VARCHAR(50) NOT NULL,
    item_id    VARCHAR(50) NOT NULL,
    quantity   INT         NOT NULL,
    unit_price DOUBLE      NOT NULL,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id)
);

CREATE TABLE IF NOT EXISTS Invoices (
    invoice_id  VARCHAR(50) PRIMARY KEY,
    order_id    VARCHAR(50) NOT NULL,
    merchant_id VARCHAR(50) NOT NULL,
    issue_date  DATE        NOT NULL,
    gross_total DOUBLE      NOT NULL,
    discount    DOUBLE      NOT NULL,
    final_total DOUBLE      NOT NULL
);