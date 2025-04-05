-- Create customers table with auto-incremented numeric ID
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customer_payments (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE RESTRICT,
    price DECIMAL(10, 2) NOT NULL,
    point DECIMAL(10, 2) NOT NULL,
    price_modifier DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    datetime TIMESTAMP WITH TIME ZONE NOT NULL,
    metadata JSONB,

    CONSTRAINT positive_price CHECK (price >= 0),
    CONSTRAINT positive_point CHECK (point >= 0),
    CONSTRAINT positive_price_modifier CHECK (price_modifier > 0),

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_customer_payments_customer_id ON customer_payments(customer_id);
CREATE INDEX idx_customer_payments_datetime ON customer_payments(datetime);
CREATE INDEX idx_customer_payments_metadata ON customer_payments USING GIN (metadata);
