CREATE TABLE partner
(
    id              BIGSERIAL PRIMARY KEY,
    partner_id      VARCHAR(255) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    website_url     VARCHAR(500),

    -- nowe dane adresowe
    city            VARCHAR(255),
    street          VARCHAR(255),
    building_number VARCHAR(50),

    company_info    VARCHAR(500),

    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE partner_configuration
(
    id                   BIGSERIAL PRIMARY KEY,
    partner_id           VARCHAR(255) NOT NULL,

    -- klucze RSA przeniesione tutaj
    public_key           TEXT         NOT NULL,
    private_key          TEXT         NOT NULL,

    webhook_paid_url     VARCHAR(500),
    webhook_cancel_url   VARCHAR(500),
    webhook_complete_url VARCHAR(500),

    created_at           TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Creates table to store commission rules for each partner

CREATE TABLE partner_commission_rule
(
    id           BIGSERIAL PRIMARY KEY,

    partner_id   VARCHAR(255) NOT NULL,

    kind         VARCHAR(50)  NOT NULL,

    added_on_top BOOLEAN      NOT NULL DEFAULT FALSE,

    rate         NUMERIC(19, 6),
    fixed_value  NUMERIC(19, 2),
    currency     VARCHAR(10),

    CONSTRAINT commission_rule_valid CHECK (
        (rate IS NOT NULL AND fixed_value IS NULL AND currency IS NULL)
            OR
        (rate IS NULL AND fixed_value IS NOT NULL AND currency IS NOT NULL)
        )
);

CREATE INDEX idx_commission_partner_id ON partner_commission_rule (partner_id);

CREATE TABLE fiat_rate_history
(
    id           BIGSERIAL PRIMARY KEY,
    currency     VARCHAR(3)     NOT NULL,
    rate         NUMERIC(18, 6) NOT NULL,
    effective_at DATE           NOT NULL,
    fetched_at   TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_fiat_rate_currency
    ON fiat_rate_history (currency);


CREATE TABLE transaction
(
    id                   BIGSERIAL PRIMARY KEY,

    partner_id           VARCHAR(255)   NOT NULL,

    currency_from        VARCHAR(3)     NOT NULL,
    currency_to          VARCHAR(3)     NOT NULL,

    amount_fiat          NUMERIC(18, 6) NOT NULL,
    partner_fee          NUMERIC(18, 6) NOT NULL,
    platform_fee         NUMERIC(18, 6) NOT NULL,
    amount_after_fee     NUMERIC(18, 6) NOT NULL,

    nbp_rate             NUMERIC(18, 6) NOT NULL,
    converted_amount     NUMERIC(18, 6) NOT NULL,

    status               VARCHAR(50)    NOT NULL,

    user_email           VARCHAR(255),
    verification_code    VARCHAR(6),
    verification_expires TIMESTAMP,

    payment_method       VARCHAR(50),

    created_at           TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_transaction_partner ON transaction (partner_id);
