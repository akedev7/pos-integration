CREATE TABLE payment_rule (
    id BIGSERIAL PRIMARY KEY,
    conditions TEXT,
    points_percentage NUMERIC(5,2) NOT NULL
);

INSERT INTO payment_rule (conditions, points_percentage) VALUES
('paymentMethod == ''CASH'' and ((priceModifier >= 0.9) and (priceModifier <= 1.0))', 5.00),
('paymentMethod == ''CASH_ON_DELIVERY'' and ((priceModifier >= 1) and (priceModifier <= 1.2)) and (additionalItem[''courier''] == ''YAMATO'' or additionalItem[''courier''] == ''SAGAWA'')', 5.00),
('paymentMethod == ''VISA'' and ((priceModifier >= 0.9) and (priceModifier <= 2.0))', 5.00),
('paymentMethod == ''MASTERCARD'' and ((priceModifier >= 0.9) and (priceModifier <= 2.0))', 5.00),
('paymentMethod == ''AMEX'' and ((priceModifier >= 0.9) and (priceModifier <= 2.0))', 5.00),
('paymentMethod == ''JCB'' and ((priceModifier >= 0.9) and (priceModifier <= 2.0))', 5.00),
('paymentMethod == ''LINE_PAY'' and ((priceModifier >= 0.9) and (priceModifier <= 2.0))', 5.00),
('paymentMethod == ''PAYPAY'' and ((priceModifier >= 0.9) and (priceModifier <= 2.0))', 5.00),
('paymentMethod == ''POINTS'' and ((priceModifier >= 0.9) and (priceModifier <= 2.0))', 5.00),
('paymentMethod == ''GRAB_PAY'' and ((priceModifier >= 0.9) and (priceModifier <= 2.0))', 5.00),
('paymentMethod == ''BANK_TRANSFER'' and ((priceModifier >= 0.9) and (priceModifier <= 2.0))', 5.00),
('paymentMethod == ''CHEQUE'' and ((priceModifier >= 0.9) and (priceModifier <= 2.0))', 5.00);