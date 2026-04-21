-- orders
INSERT INTO orders (id, customer_id, order_number, status, subtotal, discount_amount, discount_shipping_fee,
                    shipping_fee, total, payment_method, address_id,
                    receiver_name, receiver_phone, city_code, city_name, ward_code, ward_name, address_detail,
                    notes, created_at, paid_at, completed_at)
VALUES ('11111111-1111-1111-1111-111111111111',
        'ea21fdfd-9ff3-4f9f-bef2-a09f6d31baac',
        'TCS250312A1B2C3', 'PENDING',
        120000, 0, 0, 15000, 135000, 'VNPAY',
        '9d652327-242e-466a-adf6-e4dad98ff897',
        'Receiver 1', '0901234567', 79, 'Hồ Chí Minh', 26734, 'Phường Bến Nghé', '123 Nguyễn Huệ',
        'Please call before delivery', NOW(), NULL, NULL),

       ('22222222-2222-2222-2222-222222222222',
        'ea21fdfd-9ff3-4f9f-bef2-a09f6d31baac',
        'TCS250312D4E5F6', 'COMPLETED',
        250000, 50000, 0, 15000, 215000, 'VNPAY',
        '9d652327-242e-466a-adf6-e4dad98ff897',
        'Receiver 1', '0901234567', 79, 'Hồ Chí Minh', 26734, 'Phường Bến Nghé', '123 Nguyễn Huệ',
        '', NOW(), NOW(), NOW());

-- order items
INSERT INTO order_items (id, order_id, product_id, variant_id, variant_name, variant_image_url, product_name, quantity, unit_price, subtotal, discount_amount, final_amount)
VALUES
    ('a1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', '891813d3-69cb-4292-b802-24f6f0948c3b', '1ba91c2b-8407-4bec-b2f8-cee452a90c61', 'Red - L', 'https://example.com/images/nike-tshirt-red.jpg', 'Nike T-Shirt', 2, 250000, 500000, 0, 500000),
    ('a2222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', '364534a2-1cbe-48a9-a0f7-445688584575', 'b12c4217-aa13-9071-5241-a34917a81923', 'Black - M', 'https://example.com/images/adidas-pants-black.jpg', 'Adidas Pants', 1, 350000, 350000, 0, 350000),
    ('a3333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222', '364534a2-1cbe-48a9-a0f7-445688584575', 'b12c4217-aa13-9071-5241-a34917a81923', 'Black - M', 'https://example.com/images/adidas-pants-black.jpg', 'Adidas Pants', 3, 350000, 1050000, 50000, 1000000);

-- coupons
INSERT INTO m_coupon (id, code, type, value, min_order_value, max_discount, usage_limit, used_count, start_date, end_date, title, description)
VALUES
    ('dd111111-1111-1111-1111-111111111111', 'SALE20', 'PERCENT', 20, 100000, 50000, 100, 1, '2025-01-01', '2026-12-31', '20% Off', 'Discount 20% on orders above 100,000'),
    ('dd222222-2222-2222-2222-222222222222', 'FREESHIP', 'FREE_SHIP', 15000, 0, NULL, 200, 0, '2025-01-01', '2026-12-31', 'Free Shipping', 'Free shipping coupon for all orders');

-- order coupons
INSERT INTO order_coupons (id, order_id, coupon_id, discount_applied)
VALUES
    ('ac111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'dd111111-1111-1111-1111-111111111111', 50000);