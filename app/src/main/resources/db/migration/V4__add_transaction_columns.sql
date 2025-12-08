ALTER TABLE transaction
    ADD COLUMN resend_count INT NOT NULL DEFAULT 0,
    ADD COLUMN external_payment_id VARCHAR(255);