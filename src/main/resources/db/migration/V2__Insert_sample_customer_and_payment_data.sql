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
