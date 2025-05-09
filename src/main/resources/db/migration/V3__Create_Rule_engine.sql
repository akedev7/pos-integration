CREATE TABLE payment_rule (
    id BIGSERIAL PRIMARY KEY,
    payment_method VARCHAR(20) NOT NULL,
    conditions TEXT,
    points_percentage NUMERIC(5,2) NOT NULL
);

INSERT INTO payment_rule (payment_method, conditions, points_percentage) VALUES
('CASH', '((priceModifier >= 0.9) and (priceModifier <= 1.0))', 0.05),
('CASH_ON_DELIVERY', '((priceModifier >= 1) and (priceModifier <= 1.02)) and (additionalItem.get(''courier'').asText() == ''YAMATO'' or additionalItem.get(''courier'').asText() == ''SAGAWA'')', 0.03),
('VISA', '((priceModifier >= 0.95) and (priceModifier <= 1.0)) and additionalItem.get(''last4'').asText() matches ''\d{4}''', 0.03),
('MASTERCARD', '((priceModifier >= 0.95) and (priceModifier <= 1)) and additionalItem.get(''last4'').asText() matches ''\d{4}''', 0.03),
('AMEX', '((priceModifier >= 0.98) and (priceModifier <= 1.01)) and additionalItem.get(''last4'').asText() matches ''\d{4}''', 0.02),
('JCB', '((priceModifier >= 0.95) and (priceModifier <= 1.0)) and additionalItem.get(''last4'').asText() matches ''\d{4}''', 0.05),
('LINE_PAY', 'priceModifier == 1', 0.01),
('PAYPAY', 'priceModifier == 1', 0.01),
('POINTS', 'priceModifier == 1', 0),
('GRAB_PAY', 'priceModifier == 1', 0.01),
('BANK_TRANSFER', 'priceModifier == 1 and additionalItem.get(''bankName'').asText() matches ''[A-Za-z\s]+'' and additionalItem.get(''bankAccount'').asText() matches ''\d+''', 0),
('CHEQUE', '((priceModifier >= 0.9) and (priceModifier <= 1.0)) and additionalItem.get(''bankName'').asText() matches ''[A-Za-z\s]+'' and additionalItem.get(''chequeNumber'').asText() matches ''\d+''', 0);
