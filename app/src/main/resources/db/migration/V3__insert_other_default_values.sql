INSERT INTO partner (partner_id,
                     name,
                     website_url,
                     city,
                     street,
                     building_number,
                     company_info)
VALUES ('Gawel',
        'Kantor internetowy Gaweł',
        'https://kantor-gawel.test',
        'Poznań',
        'Święty Marcin',
        '12A',
        'Specjalizuje się w wymianie PLN/EUR/USD.');

INSERT INTO partner_configuration (
    partner_id,
    public_key,
    private_key,
    webhook_paid_url,
    webhook_cancel_url,
    webhook_complete_url
) VALUES (
             'Gawel',
             '-----BEGIN PUBLIC KEY-----
         MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwTESTPUBLICKEYFORGAWEL
         uH3Qm8b1xQqK12p0iYwqv0QvL4W0xq9yK2vA7Y2nqfWTfKe8DOujZC9u+1i5JrYz
         3fFmNf4ZtG7Hq9Zp8kq2oBq3d2b5i4w6tYy3p5Y1v0w3l7L1dVQIDAQAB
         -----END PUBLIC KEY-----',
             '-----BEGIN PRIVATE KEY-----
         MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDAT0ESTPRIVATEKEY
         FORGAWELONLYFORDEVENVIRONMENTDUMMYDATAHEREPLEASEGENERATEYOURREALKEYS
         OUTSIDETHISSQLANDPASTEPROPERPEMCONTENTINTHESEDATABASEFIELDS
         -----END PRIVATE KEY-----',
             'https://partner-gawel.test/webhook/paid',
             'https://partner-gawel.test/webhook/cancel',
             'https://partner-gawel.test/webhook/complete'
         );

INSERT INTO transaction (
    partner_id,
    currency_from,
    currency_to,
    amount_fiat,
    partner_fee,
    platform_fee,
    amount_after_fee,
    nbp_rate,
    converted_amount,
    status,
    user_email,
    verification_code,
    verification_expires,
    payment_method
) VALUES (
             'Gawel',          -- partner_id
             'EUR',            -- currency_from (kupujesz)
             'PLN',            -- currency_to (za)
             100.000000,       -- amount_fiat (kwota wejściowa EUR)
             2.000000,         -- partner_fee
             1.000000,         -- platform_fee
             97.000000,        -- amount_after_fee  (= 100 - 2 - 1)
             4.300000,         -- nbp_rate (przykładowy EUR→PLN)
             417.100000,       -- converted_amount (97 * 4.3)
             'PENDING',        -- status
             'user@example.com',
             '123456',
             NOW() + INTERVAL '15 minutes',
             'BANK_TRANSFER'
         );
