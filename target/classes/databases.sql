CREATE TABLE companies (
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL UNIQUE,  -- Nombre de la empresa
    legal_name      VARCHAR(255),                  -- Razón social
    tax_id          VARCHAR(50) UNIQUE,            -- NIT / RUC / CIF, etc.
    email          VARCHAR(255) UNIQUE,           -- Correo de contacto
    phone          VARCHAR(50),                   -- Teléfono de contacto
    address        TEXT,                          -- Dirección
    country        VARCHAR(100),                  -- País
    state          VARCHAR(100),                  -- Estado/Provincia
    city           VARCHAR(100),                  -- Ciudad
    website        VARCHAR(255),                  -- Sitio web de la empresa
    logo_url       VARCHAR(500),                  -- URL del logo de la empresa
    subscription_plan VARCHAR(100),               -- Plan de suscripción (Free, Pro, Enterprise)
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active      BOOLEAN DEFAULT TRUE           -- Estado activo/inactivo
);

CREATE TABLE subscriptions (
    id             SERIAL PRIMARY KEY,
    company_id     BIGINT NOT NULL,  -- Relación con la empresa
    plan_name      VARCHAR(100) NOT NULL,  -- Plan (Free, Pro, company)
    price          DECIMAL(10,2) NOT NULL,  -- Precio del plan
    start_date     DATE NOT NULL,  -- Fecha de inicio de la suscripción
    end_date       DATE NOT NULL,  -- Fecha de vencimiento
    is_active      BOOLEAN DEFAULT TRUE,  -- Si la suscripción está activa
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
);

CREATE TABLE payments (
    id             SERIAL PRIMARY KEY,
    company_id     BIGINT NOT NULL,  -- Empresa que realizó el pago
    amount         DECIMAL(10,2) NOT NULL,  -- Monto pagado
    payment_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Fecha de pago
    payment_status VARCHAR(10) NOT NULL,  -- Estado del pago
    transaction_id VARCHAR(255) UNIQUE,  -- ID de la transacción con el proveedor de pagos
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
);


CREATE TABLE persons (
    id SERIAL PRIMARY KEY,
    dni VARCHAR(255),
    names VARCHAR(255),
    surnames VARCHAR(255),
    phone VARCHAR(255) UNIQUE,
    address VARCHAR(255),
    birth DATE
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    company_id  BIGINT NOT NULL,
    email VARCHAR(255) UNIQUE,
    username VARCHAR(255) UNIQUE,
    password VARCHAR(255) NOT NULL,
    person_id INT UNIQUE, -- Relación 1 a 1 con persons
    role_id INT, -- Relación con roles
    is_enable BOOLEAN DEFAULT TRUE,
    account_not_expired BOOLEAN,
    account_not_locked BOOLEAN,
    credential_not_expired BOOLEAN,
    CONSTRAINT fk_users_person FOREIGN KEY (person_id) REFERENCES persons(id) ON DELETE CASCADE,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id),
    CONSTRAINT fk_users_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    role VARCHAR(255),
    validator VARCHAR(50),
    description VARCHAR(255),
    role_name VARCHAR(50),
    status BOOLEAN
);

CREATE TABLE IF NOT exists permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT exists menus (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    name_father VARCHAR(255) NOT NULL,
    father VARCHAR(255),
    route VARCHAR(255),
    menu_order INT,
    icon VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS role_permission (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS menuroles(
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, menu_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_id) REFERENCES menus(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS configparams(
    id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    shortname VARCHAR(50) NOT NULL,
    parent VARCHAR(50) NOT NULL,
    status INT NOT NULL
);

INSERT INTO "companies" ("id", "name", "legal_name", "tax_id", "email", "phone", "address", "country", "state", "city", "website", "logo_url", "subscription_plan", "created_at", "is_active") VALUES
	(1, 'AureaLab', 'AureaLab', '1', 'aurealab@gmail.com', '3023424366', 'Cra 24a # 54-16', 'COLOMBIA', 'VALLE DEL CAUCA', 'CALI', 'www.aurealab.com.co', NULL, NULL, '2025-03-07 10:28:23.544293', 'true');

WITH inserted_person AS (
    INSERT INTO persons (dni, names, surnames, phone, address, birth)
    VALUES ('12345678', 'William', 'Castaño', '3023424366', 'Via La Buitrera', '1994-10-05')
    RETURNING id
)

INSERT INTO users (email, username, company_id, password, person_id, role_id, is_enable, account_not_expired, account_not_locked, credential_not_expired)
SELECT 'williamisrael210@gmail.com', 'willcast', 1, '$2a$10$q5rtm2jyXUVOpY.2hvR7OOcz9wQcuAgOKAXdOyjyt2x0tcrJayOUy', id, 1, TRUE, TRUE, TRUE, TRUE
FROM inserted_person;


INSERT INTO permissions (name) VALUES ('READ'), ('CREATE'), ('UPDATE'), ('DELETE'), ('SUPERUSER');

INSERT INTO roles (role, description, role_name, status) VALUES
('superuser', 'Este rol tiene permisos globales sobre toda la aplicación', 'Super Usuario', TRUE),
('admin', 'Este rol tiene privilegios elevados', 'Administrador', TRUE),
('supervisor', 'Este rol esta mas enfocado en reportes y métricas', 'Supervisor', TRUE),
('operative', 'Usuario operativo con capacidad de gestionar turnos', 'Operativo', TRUE),
('digiter', 'Este rol solo ingresará datos', 'Digitador', TRUE);

INSERT INTO role_permission (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
(2, 1), (2, 2), (2, 3), (2, 4),
(3, 1), (3, 2), (3, 3),
(4, 1), (4, 2),
(5, 1);

INSERT INTO menus (name, father, name_father, route, menu_order, icon) VALUES
('Asesores', 'mercadeo','Mercadeo', 'mercadeo/asesores', 1, NULL),
('Contacto de clientes', 'mercadeo','Mercadeo', 'mercadeo/contactarclientes', 2, NULL),
('Ventas', 'mercadeo','Mercadeo', 'mercadeo/ventas', 1, NULL),
('Llamadas', 'mercadeo','Mercadeo', 'mercadeo/llamadas', 2, NULL),
('Mi cuenta', 'configuracion','Configuracion', 'configuracion/micuenta', 1, NULL);

INSERT INTO menuroles (role_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5),
(3, 1), (3, 2), (3, 3), (3, 4), (3, 5),
(4, 1), (4, 2), (4, 3), (4, 4), (4, 5),
(5, 1), (5, 2), (5, 3), (5, 4), (5, 5);