-- Attributes
INSERT INTO m_attribute (id, name, code, sort_order, status)
VALUES
    ('2998c3b9-fd23-46ca-bceb-584a755c6d4d', 'Color', 'COLOR', 1, 'ACTIVE'),
    ('870f539e-8b73-4281-a0c7-a335bd7bc18d', 'Size', 'SIZE', 2, 'ACTIVE');

INSERT INTO m_attribute_value (id, attribute_id, value, code, sort_order, status)
VALUES
    ('ae430920-8f07-4485-867d-5839d886c91f', '2998c3b9-fd23-46ca-bceb-584a755c6d4d', 'Red', 'RED', 1, 'ACTIVE'),
    ('d9f131fd-be27-4233-802a-c60d0ffb4e91', '2998c3b9-fd23-46ca-bceb-584a755c6d4d', 'Blue', 'BLUE', 2, 'ACTIVE'),
    ('957b9730-bc79-4694-a1b3-63ae5b29669e', '870f539e-8b73-4281-a0c7-a335bd7bc18d', 'S', 'S', 1, 'ACTIVE'),
    ('66275122-9ad8-4caf-bf2d-1cf923df8681', '870f539e-8b73-4281-a0c7-a335bd7bc18d', 'M', 'M', 2, 'ACTIVE'),
    ('48af02f9-7760-4af5-ad2f-77bbeffa5313', '870f539e-8b73-4281-a0c7-a335bd7bc18d', 'L', 'L', 3, 'ACTIVE');

-- Categories
INSERT INTO m_category (id, parent_id, name, slug)
VALUES
    ('33daddfa-02eb-4879-8168-1312f03e4322', NULL, 'Fashion', 'fashion'),
    ('9b8556ce-1040-4a54-ad80-48d4331e9fdb', '33daddfa-02eb-4879-8168-1312f03e4322', 'Shirts', 'shirts'),
    ('c018322e-46f1-4b6e-a80b-3a5d17cc2589', '33daddfa-02eb-4879-8168-1312f03e4322', 'Pants', 'pants');

-- Brands
INSERT INTO m_brand (id, name, slug, description)
VALUES
    ('a62046d5-33f6-4512-844c-73ecfd7db848', 'Nike', 'nike', NULL),
    ('c5012948-a3f8-4373-aa87-a3f8007fc612', 'Adidas', 'adidas', NULL);

-- Products
INSERT INTO m_product (id, name, slug, category_id, brand_id, min_price, max_price, status)
VALUES
    ('891813d3-69cb-4292-b802-24f6f0948c3b', 'Nike T-Shirt', 'nike-t-shirt', '9b8556ce-1040-4a54-ad80-48d4331e9fdb', 'a62046d5-33f6-4512-844c-73ecfd7db848', 250000, 250000, 'ACTIVE'),
    ('364534a2-1cbe-48a9-a0f7-445688584575', 'Adidas Pants', 'adidas-pants', 'c018322e-46f1-4b6e-a80b-3a5d17cc2589', 'c5012948-a3f8-4373-aa87-a3f8007fc612', 350000, 350000, 'ACTIVE');

-- Product Variants
INSERT INTO product_variants (id, product_id, sku, price, compare_at_price, image_url, status)
VALUES
    ('1ba91c2b-8407-4bec-b2f8-cee452a90c61', '891813d3-69cb-4292-b802-24f6f0948c3b', 'NIKE-RED-S', 250000, NULL, NULL, 'ACTIVE'),
    ('bf1c7947-ca60-4862-8771-d084e9641307', '891813d3-69cb-4292-b802-24f6f0948c3b', 'NIKE-BLUE-M', 250000, NULL, NULL, 'ACTIVE'),
    ('b12c4217-aa13-9071-5241-a34917a81923', '364534a2-1cbe-48a9-a0f7-445688584575', 'ADIDAS-RED-L', 350000, NULL, NULL, 'ACTIVE');

-- Product Variant Attribute Values
INSERT INTO product_variant_attribute_value (id, variant_id, attribute_value_id)
VALUES
    ('409ef3bc-591c-4e4a-ab4c-102a9bb1667e', '1ba91c2b-8407-4bec-b2f8-cee452a90c61', 'ae430920-8f07-4485-867d-5839d886c91f'),
    ('8959591f-bc19-4076-bc02-20ebcf210963', '1ba91c2b-8407-4bec-b2f8-cee452a90c61', '957b9730-bc79-4694-a1b3-63ae5b29669e'),
    ('9d8f62d3-5602-4dcc-822e-2543db7ee569', 'bf1c7947-ca60-4862-8771-d084e9641307', 'd9f131fd-be27-4233-802a-c60d0ffb4e91'),
    ('dde60b3a-3fe9-46cf-9bc3-446d457bf28f', 'bf1c7947-ca60-4862-8771-d084e9641307', '66275122-9ad8-4caf-bf2d-1cf923df8681'),
    ('dde60b3a-3fe9-46cf-9bc3-446d457bf28e', 'b12c4217-aa13-9071-5241-a34917a81923', 'ae430920-8f07-4485-867d-5839d886c91f'),
    ('dde60b3a-3fe9-46cf-9bc3-446d457bf29a', 'b12c4217-aa13-9071-5241-a34917a81923', '48af02f9-7760-4af5-ad2f-77bbeffa5313');

-- Inventory
INSERT INTO product_variant_inventory (id, variant_id, quantity, reserved_quantity, low_stock_threshold, manufacture_date, expiry_date, sold_quantity)
VALUES
    ('17e77712-997a-4210-9cd6-e81c30531118', '1ba91c2b-8407-4bec-b2f8-cee452a90c61', 100, 0, 10, '2025-01-01', '2027-01-01', 0),
    ('29fa6c33-d1c4-4592-acac-1f77431ab801', 'bf1c7947-ca60-4862-8771-d084e9641307', 50, 0, 10, '2025-01-01', '2027-01-01', 0),
    ('36db12d9-a2b4-1344-ab12-1682331ab735', 'b12c4217-aa13-9071-5241-a34917a81923', 75, 0, 10, '2025-01-01', '2027-01-01', 0);

-- Product Image
INSERT INTO product_images (id, image_url, sort_order, status, product_id, public_id)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'https://img.com/p1.jpg', 0, 'TEMP', NULL, '1'),
    ('22222222-2222-2222-2222-222222222222', 'https://img.com/p2.jpg', 1, 'TEMP', NULL, '2'),
    ('33333333-3333-3333-3333-333333333333', 'https://img.com/p3.jpg', 2, 'TEMP', NULL, '3');