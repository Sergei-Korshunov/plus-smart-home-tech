CREATE TABLE IF NOT EXISTS products (
    product_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    image_src VARCHAR(1000),
    category VARCHAR(20),
    availability VARCHAR(20) NOT NULL,
    state VARCHAR(20) NOT NULL,
    rating DOUBLE PRECISION,
    price DOUBLE PRECISION
);