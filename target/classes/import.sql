INSERT INTO financial_account (name, balance) VALUES ( 'Edoardo', 1000.0);
INSERT INTO financial_account (name, balance) VALUES ( 'Andreia', 2000.0);

INSERT INTO transaction (timestamp, amount, description, account_id) VALUES ('2025-06-15 11:00:00', 150.00, 'Pagamento bolletta', 1);

INSERT INTO transaction (timestamp, amount, description, account_id) VALUES ('2025-06-15 12:30:00', 250.50, 'Bonifico ad Andrea', 1);