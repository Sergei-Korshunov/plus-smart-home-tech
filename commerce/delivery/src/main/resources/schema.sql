CREATE TABLE IF NOT EXISTS address (
    address_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    country VARCHAR(20) NOT NULL,
    city VARCHAR(50) NOT NULL,
    street VARCHAR(50) NOT NULL,
    house VARCHAR(20) NOT NULL,
    flat VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS delivery (
    delivery_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    delivery_volume DOUBLE PRECISION,
    delivery_weight DOUBLE PRECISION,
    fragile BOOLEAN,
    from_address_id UUID NOT NULL,
    to_address_id UUID NOT NULL,
    state varchar(20) NOT NULL,
    order_id UUID NOT NULL,

    CONSTRAINT delivery_from_address_fk FOREIGN KEY (from_address_id) REFERENCES address(address_id) ON DELETE CASCADE,
    CONSTRAINT delivery_to_address_fk FOREIGN KEY (to_address_id) REFERENCES address(address_id) ON DELETE CASCADE
);