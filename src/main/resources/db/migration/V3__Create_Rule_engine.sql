CREATE TABLE payment_rule (
    id BIGSERIAL PRIMARY KEY,
    conditions TEXT,
    min_discount_percentage NUMERIC(5,2) NOT NULL,
    max_discount_percentage NUMERIC(5,2) NOT NULL,
    points_percentage NUMERIC(5,2) NOT NULL
);

INSERT INTO payment_rule (conditions, min_discount_percentage, max_discount_percentage, points_percentage) VALUES
('paymentMethod == ''CASH'' and ((priceModifier >= 0.9) and (priceModifier <= 2.0))', 10.00, 0.00, 5.00),
('paymentMethod == ''CASH_ON_DELIVERY'' && (additionalItem[''courier''] == ''YAMATO'' || additionalItem[''courier''] == ''SAGAWA'')', 0.00, 10.00, 5.00),
('paymentMethod == ''VISA''', 10.00, 0.00, 5.00),
('paymentMethod == ''MASTERCARD''', 10.00, 0.00, 5.00),
('paymentMethod == ''AMEX''', 10.00, 0.00, 5.00),
('paymentMethod == ''JCB''', 10.00, 0.00, 5.00),
('paymentMethod == ''LINE_PAY''', 10.00, 0.00, 5.00),
('paymentMethod == ''PAYPAY''', 10.00, 0.00, 5.00),
('paymentMethod == ''POINTS''', 10.00, 0.00, 5.00),
('paymentMethod == ''GRAB_PAY''', 10.00, 0.00, 5.00),
('paymentMethod == ''BANK_TRANSFER''', 10.00, 0.00, 5.00),
('paymentMethod == ''CHEQUE''', 10.00, 0.00, 5.00);