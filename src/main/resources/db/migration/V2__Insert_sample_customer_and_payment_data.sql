-- Insert sample customer data (IDs will be auto-generated)
INSERT INTO customers (first_name, last_name, email, phone, address) VALUES
('John', 'Smith', 'john.smith@example.com', '+1234567890',
 '{"street": "123 Main St", "city": "New York", "state": "NY", "zip": "10001", "country": "USA"}'),
('Emily', 'Johnson', 'emily.j@example.com', '+1987654321',
 '{"street": "456 Oak Ave", "city": "Los Angeles", "state": "CA", "zip": "90001", "country": "USA"}'),
('Michael', 'Brown', 'michael.b@example.com', '+1122334455',
 '{"street": "789 Pine Rd", "city": "Chicago", "state": "IL", "zip": "60601", "country": "USA"}'),
('Test', 'Customer', 'test.customer@example.com', '+1555123456',
 '{"street": "321 Elm St", "city": "Austin", "state": "TX", "zip": "73301", "country": "USA"}');

-- Insert sample payment data (using the generated customer IDs)
INSERT INTO customer_payments (customer_id, price, price_modifier, payment_method, datetime, metadata) VALUES
(1, 100.00, 1.0, 'CREDIT_CARD', '2023-01-15T10:30:00Z',
 '{"items": [{"product_id": "P100", "quantity": 2}], "discount_code": "SPRING10"}'),
(1, 75.50, 0.9, 'PAYPAL', '2023-02-20T14:45:00Z',
 '{"items": [{"product_id": "P205", "quantity": 1}], "shipping_method": "express"}'),
(2, 200.00, 1.0, 'BANK_TRANSFER', '2023-03-05T09:15:00Z',
 '{"items": [{"product_id": "P300", "quantity": 3}], "notes": "Corporate account"}'),
(3, 150.25, 1.05, 'CREDIT_CARD', '2023-03-10T16:20:00Z',
 '{"items": [{"product_id": "P150", "quantity": 1}, {"product_id": "P180", "quantity": 2}], "courier": "FEDEX"}'),
(4, 100.00, 1.0, 'CASH_ON_DELIVERY', '2022-09-01T00:00:00Z',
 '{"courier": "YAMATO", "delivery_notes": "Leave at front door"}'),
(2, 89.99, 0.95, 'CREDIT_CARD', '2023-04-01T11:10:00Z',
 '{"items": [{"product_id": "P400", "quantity": 2}], "membership_level": "gold"}');