-- Thêm cột status vào roles
ALTER TABLE roles
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
