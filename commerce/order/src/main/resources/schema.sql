CREATE TABLE IF NOT EXISTS orders (
    order_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    state VARCHAR(20) NOT NULL,
    cart_id UUID NOT NULL,
    delivery_id UUID,
    payment_id UUID,
    delivery_volume DOUBLE PRECISION,
    delivery_weight DOUBLE PRECISION,
    fragile BOOLEAN,
    total_price DOUBLE PRECISION,
    product_price DOUBLE PRECISION,
    delivery_price DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS order_products (
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity BIGINT,
    CONSTRAINT order_products_pk PRIMARY KEY (order_id, product_id),
    CONSTRAINT order_products_cart_fk FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);