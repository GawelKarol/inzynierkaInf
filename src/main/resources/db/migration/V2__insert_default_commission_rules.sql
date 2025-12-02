
INSERT INTO partner_commission_rule (
    partner_id, kind, added_on_top, rate, fixed_value, currency
) VALUES
      ('Gawel', 'BASIC_FIXED', FALSE, NULL, 5.00, 'PLN'),
      ('Gawel', 'BASIC_FIXED', FALSE, NULL, 1.00, 'EUR'),
      ('Gawel', 'BASIC_FIXED', FALSE, NULL, 1.50, 'USD');

INSERT INTO partner_commission_rule (
    partner_id, kind, added_on_top, rate, fixed_value, currency
) VALUES
    ('Gawel', 'SERVICE_PROVIDER', FALSE, 0.02, NULL, NULL);

INSERT INTO partner_commission_rule (
    partner_id, kind, added_on_top, rate, fixed_value, currency
) VALUES
    ('Gawel', 'PARTNER', TRUE, 0.01, NULL, NULL);
