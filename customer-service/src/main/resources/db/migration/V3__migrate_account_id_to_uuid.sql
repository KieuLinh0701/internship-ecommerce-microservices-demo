-- 1. Thêm cột UUID mới
ALTER TABLE customers ADD COLUMN account_uuid UUID;

-- 2. Drop cột cũ
ALTER TABLE customers DROP COLUMN account_id;

-- 3. Đổi tên cột mới thành account_id
ALTER TABLE customers RENAME COLUMN account_uuid TO account_id;

-- 4. Đặt NOT NULL cho account_id
ALTER TABLE customers ALTER COLUMN account_id SET NOT NULL;
