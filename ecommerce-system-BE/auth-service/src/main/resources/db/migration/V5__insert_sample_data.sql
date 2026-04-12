-- Roles
INSERT INTO m_role (id, name, status, is_system)
VALUES ('26fdbe5d-6014-42f2-80d9-5b913cd61eb0', 'ADMIN', 'ACTIVE', true),
       ('d6a49443-9709-481d-bb93-6d9b200db498', 'CUSTOMER', 'ACTIVE', true);

-- Accounts (Password: MatKhau@001)
INSERT INTO accounts (id, email, password, status)
VALUES ('259db6a2-8c7a-45ea-b1da-bb97f07c2816', 'admin@gmail.com',
        '$2a$10$/UogER/afQJYWGhLGab1XuEjTdmFr.CP1cxOqearRxIgNcIU0nqsO', 'ACTIVE'),
       ('63409e4b-e9c4-48cc-b641-1419737e13ef', 'customer@gmail.com',
        '$2a$10$/UogER/afQJYWGhLGab1XuEjTdmFr.CP1cxOqearRxIgNcIU0nqsO', 'PENDING');

-- Account Roles
INSERT INTO account_roles (account_id, role_id, status)
VALUES ('259db6a2-8c7a-45ea-b1da-bb97f07c2816', '26fdbe5d-6014-42f2-80d9-5b913cd61eb0', 'ACTIVE'),
       ('63409e4b-e9c4-48cc-b641-1419737e13ef', 'd6a49443-9709-481d-bb93-6d9b200db498', 'ACTIVE');