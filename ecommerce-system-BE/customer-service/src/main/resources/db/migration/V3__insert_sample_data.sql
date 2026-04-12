-- Customers
INSERT INTO customers (id, account_id, full_name, phone)
VALUES ('ea21fdfd-9ff3-4f9f-bef2-a09f6d31baac', '63409e4b-e9c4-48cc-b641-1419737e13ef', 'Customer Test',
        '0123456789');

-- Customer Addresses
INSERT INTO customer_addresses (id, customer_id, name, phone, city_code, city_name, ward_code, ward_name, detail,
                                is_default)
VALUES ('9d652327-242e-466a-adf6-e4dad98ff897', 'ea21fdfd-9ff3-4f9f-bef2-a09f6d31baac', 'Receiver 1', '0901234567', 79,
        'Hồ Chí Minh', 26734, 'Phường Bến Nghé', '123 Nguyễn Huệ', TRUE),
       ('41af7549-3ae4-450c-bb2d-7073ce1fc801', 'ea21fdfd-9ff3-4f9f-bef2-a09f6d31baac', 'Receiver 2', '0273819273',
        1, 'Hà Nội', 26123, 'Hàm Nghi', '1 Hải Thượng Lãn Ông', FALSE);