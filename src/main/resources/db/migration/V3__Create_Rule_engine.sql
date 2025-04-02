CREATE TABLE payment_rule (
    id BIGSERIAL PRIMARY KEY,
    conditions TEXT,
    points_percentage NUMERIC(5,2) NOT NULL
);

INSERT INTO payment_rule (conditions, points_percentage) VALUES
('paymentMethod == ''CASH'' and ((priceModifier >= 0.9) and (priceModifier <= 1.0))', 0.05),
('paymentMethod == ''CASH_ON_DELIVERY'' and ((priceModifier >= 1) and (priceModifier <= 1.02)) and (additionalItem[''courier''] == ''YAMATO'' or additionalItem[''courier''] == ''SAGAWA'')', 0.03),
('paymentMethod == ''VISA'' and ((priceModifier >= 0.95) and (priceModifier <= 1.0)) and additionalItem.fieldsMap[''last4''].stringValue matches ''\d{4}''', 0.03),
('paymentMethod == ''MASTERCARD'' and ((priceModifier >= 0.95) and (priceModifier <= 1)) and additionalItem.fieldsMap[''last4''].stringValue matches ''\d{4}''', 0.03),
('paymentMethod == ''AMEX'' and ((priceModifier >= 0.98) and (priceModifier <= 1.01)) and additionalItem.fieldsMap[''last4''].stringValue matches ''\d{4}''', 0.02),
('paymentMethod == ''JCB'' and ((priceModifier >= 0.95) and (priceModifier <= 1.0)) and additionalItem.fieldsMap[''last4''].stringValue matches ''\d{4}''', 0.05),
('paymentMethod == ''LINE_PAY'' and priceModifier == 1', 0.01),
('paymentMethod == ''PAYPAY'' and priceModifier == 1', 0.01),
('paymentMethod == ''POINTS'' and priceModifier == 1', 0),
('paymentMethod == ''GRAB_PAY'' and priceModifier == 1', 0.01),
('paymentMethod == ''BANK_TRANSFER'' and priceModifier == 1', 0),
('paymentMethod == ''CHEQUE'' and ((priceModifier >= 0.9) and (priceModifier <= 1.0))', 0);