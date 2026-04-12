INSERT INTO carts (id, customer_id)
VALUES ('044df49b-7f21-4321-8785-2edc1930630d', 'ea21fdfd-9ff3-4f9f-bef2-a09f6d31baac');

-- Cart Items
INSERT INTO cart_items (id, cart_id, product_id, variant_id, quantity, status)
VALUES
    (gen_random_uuid(), '044df49b-7f21-4321-8785-2edc1930630d',
     '891813d3-69cb-4292-b802-24f6f0948c3b',
     '1ba91c2b-8407-4bec-b2f8-cee452a90c61', 2, 'ACTIVE'),

    (gen_random_uuid(), '044df49b-7f21-4321-8785-2edc1930630d',
     '891813d3-69cb-4292-b802-24f6f0948c3b',
     'bf1c7947-ca60-4862-8771-d084e9641307', 1, 'ACTIVE'),

    (gen_random_uuid(), '044df49b-7f21-4321-8785-2edc1930630d',
     '364534a2-1cbe-48a9-a0f7-445688584575',
     'b12c4217-aa13-9071-5241-a34917a81923', 3, 'ACTIVE');