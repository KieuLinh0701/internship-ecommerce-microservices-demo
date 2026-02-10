-- 1. Thêm cột UUID mới
ALTER TABLE customers ADD COLUMN uuid UUID;
ALTER TABLE addresses ADD COLUMN uuid UUID;

-- 2. Gán UUID cho dữ liệu cũ
UPDATE customers SET uuid = gen_random_uuid();
UPDATE addresses SET uuid = gen_random_uuid();

-- 3. Thêm cột UUID cho quan hệ
ALTER TABLE addresses ADD COLUMN customer_uuid UUID;

-- 4. Map UUID từ bảng cha (customers -> addresses)
UPDATE addresses ad
SET customer_uuid = c.uuid
    FROM customers c
WHERE ad.customer_id = c.id;

-- 5. Drop khóa ngoại cũ
ALTER TABLE addresses DROP CONSTRAINT fk_customer;

-- 6. Drop PK cũ
ALTER TABLE addresses DROP CONSTRAINT addresses_pkey;
ALTER TABLE customers DROP CONSTRAINT customers_pkey;

-- 7. Drop cột ID cũ
ALTER TABLE addresses DROP COLUMN customer_id;
ALTER TABLE customers DROP COLUMN id;
ALTER TABLE addresses DROP COLUMN id;

-- 8. Đổi tên cột UUID thành id
ALTER TABLE customers RENAME COLUMN uuid TO id;
ALTER TABLE addresses RENAME COLUMN uuid TO id;
ALTER TABLE addresses RENAME COLUMN customer_uuid TO customer_id;

-- 9. Tạo lại PK + FK
ALTER TABLE customers ADD PRIMARY KEY (id);
ALTER TABLE addresses ADD PRIMARY KEY (id);

ALTER TABLE addresses
    ADD CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customers(id);

-- 10. (Giữ lại unique index cũ)
CREATE UNIQUE INDEX uq_customer_default_address
    ON addresses(customer_id)
    WHERE is_default = TRUE;