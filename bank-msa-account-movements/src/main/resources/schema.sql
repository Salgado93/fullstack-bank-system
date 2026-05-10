-- ==============================================
-- Script de inicialización de base de datos
-- Banco API - PostgreSQL
-- ==============================================

-- Tabla Personas (clase base)
CREATE TABLE IF NOT EXISTS personas (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    genero VARCHAR(20),
    edad INTEGER,
    identificacion VARCHAR(20) NOT NULL UNIQUE,
    estado BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla Clientes (hereda de Persona, JOINED)
CREATE TABLE IF NOT EXISTS clientes (
    persona_id BIGINT PRIMARY KEY REFERENCES personas(id) ON DELETE CASCADE,
    cliente_id VARCHAR(50) NOT NULL UNIQUE,
    direccion VARCHAR(200),
    telefono VARCHAR(20),
    password VARCHAR(255) NOT NULL
);

-- Tabla Cuentas
CREATE TABLE IF NOT EXISTS cuentas (
    cuenta_id BIGSERIAL PRIMARY KEY,
    numero_cuenta VARCHAR(20) NOT NULL UNIQUE,
    tipo_cuenta VARCHAR(20) NOT NULL CHECK (tipo_cuenta IN ('AHORROS', 'CORRIENTE')),
    saldo_inicial NUMERIC(15, 2) NOT NULL DEFAULT 0,
    saldo NUMERIC(15, 2) NOT NULL DEFAULT 0,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    cliente_id BIGINT NOT NULL REFERENCES personas(id) ON DELETE CASCADE
);

-- Tabla Movimientos
CREATE TABLE IF NOT EXISTS movimientos (
    movimiento_id BIGSERIAL PRIMARY KEY,
    fecha TIMESTAMP NOT NULL DEFAULT NOW(),
    tipo_movimiento VARCHAR(20) NOT NULL CHECK (tipo_movimiento IN ('CREDITO', 'DEBITO')),
    valor NUMERIC(15, 2) NOT NULL,
    saldo_disponible NUMERIC(15, 2) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    cuenta_id BIGINT NOT NULL REFERENCES cuentas(cuenta_id) ON DELETE CASCADE
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_movimientos_fecha ON movimientos(fecha);
CREATE INDEX IF NOT EXISTS idx_movimientos_cuenta ON movimientos(cuenta_id);
CREATE INDEX IF NOT EXISTS idx_cuentas_cliente ON cuentas(cliente_id);

-- ==============================================
-- Datos de ejemplo
-- ==============================================

-- Insertar personas/clientes de ejemplo
INSERT INTO personas (nombre, genero, edad, identificacion, estado)
VALUES
    ('Jose Lema', 'Masculino', 30, '1234567890', true),
    ('Marianela Montalvo', 'Femenino', 28, '0987654321', true),
    ('Juan Osorio', 'Masculino', 35, '1122334455', true)
;

INSERT INTO clientes (persona_id, cliente_id, direccion, telefono, password)
SELECT id, 'CLI-' || LPAD(id::text, 4, '0'), 'Otavalo sn y principal', '098254785', 'clave123'
FROM personas WHERE identificacion = '1234567890'
;

INSERT INTO clientes (persona_id, cliente_id, direccion, telefono, password)
SELECT id, 'CLI-' || LPAD(id::text, 4, '0'), 'Amazonas y NNUU', '097548965', 'clave456'
FROM personas WHERE identificacion = '0987654321'
;

INSERT INTO clientes (persona_id, cliente_id, direccion, telefono, password)
SELECT id, 'CLI-' || LPAD(id::text, 4, '0'), '13 junio y Equinoccial', '098874587', 'clave789'
FROM personas WHERE identificacion = '1122334455'
;

-- Insertar cuentas de ejemplo
INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo, estado, cliente_id)
SELECT '478758', 'AHORROS', 2000.00, 2000.00, true, id FROM personas WHERE identificacion = '1234567890'
;

INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo, estado, cliente_id)
SELECT '225487', 'CORRIENTE', 100.00, 100.00, true, id FROM personas WHERE identificacion = '0987654321'
;

INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo, estado, cliente_id)
SELECT '495878', 'AHORROS', 0.00, 0.00, true, id FROM personas WHERE identificacion = '1122334455'
;

INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo, estado, cliente_id)
SELECT '496825', 'AHORROS', 540.00, 540.00, true, id FROM personas WHERE identificacion = '0987654321'
;
