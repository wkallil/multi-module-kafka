CREATE TABLE backorders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL,
    product_code VARCHAR(50) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    requested_quantity INTEGER NOT NULL,
    missing_quantity INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_backorders_order_number ON backorders(order_number);
CREATE INDEX idx_backorders_product_code ON backorders(product_code);
CREATE INDEX idx_backorders_status ON backorders(status);