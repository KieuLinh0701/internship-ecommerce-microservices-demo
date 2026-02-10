-- 1. Thêm cột UUID mới
ALTER TABLE accounts ADD COLUMN uuid UUID;
ALTER TABLE roles ADD COLUMN uuid UUID;

-- 2. Gán UUID tạm thời cho dữ liệu cũ
UPDATE accounts SET uuid = gen_random_uuid();
UPDATE roles SET uuid = gen_random_uuid();

-- 3. Thêm cột UUID vào bảng trung gian
ALTER TABLE account_roles ADD COLUMN account_uuid UUID;
ALTER TABLE account_roles ADD COLUMN role_uuid UUID;

-- 4. Map UUID từ bảng cha sang bảng trung gian
UPDATE account_roles ar
SET account_uuid = a.uuid
    FROM accounts a
WHERE ar.account_id = a.id;

UPDATE account_roles ar
SET role_uuid = r.uuid
    FROM roles r
WHERE ar.role_id = r.id;

-- 5. Drop khóa ngoại cũ
ALTER TABLE account_roles DROP CONSTRAINT fk_account;
ALTER TABLE account_roles DROP CONSTRAINT fk_role;

-- 6. Drop PK cũ
ALTER TABLE account_roles DROP CONSTRAINT account_roles_pkey;

-- 7. Drop cột id cũ
ALTER TABLE account_roles DROP COLUMN account_id;
ALTER TABLE account_roles DROP COLUMN role_id;
ALTER TABLE accounts DROP COLUMN id;
ALTER TABLE roles DROP COLUMN id;

-- 8. Đổi tên cột UUID thành id
ALTER TABLE accounts RENAME COLUMN uuid TO id;
ALTER TABLE roles RENAME COLUMN uuid TO id;
ALTER TABLE account_roles RENAME COLUMN account_uuid TO account_id;
ALTER TABLE account_roles RENAME COLUMN role_uuid TO role_id;

-- 9. Tạo lại PK + FK
ALTER TABLE accounts ADD PRIMARY KEY (id);
ALTER TABLE roles ADD PRIMARY KEY (id);
ALTER TABLE account_roles ADD PRIMARY KEY (account_id, role_id);

ALTER TABLE account_roles
    ADD CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES accounts(id),
    ADD CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles(id);
