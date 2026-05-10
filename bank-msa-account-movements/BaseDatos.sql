-- ================================================================
-- BaseDatos.sql
-- Script de creación de base de datos, entidades y datos iniciales
-- Proyecto: Banco API REST - bank-msa-account-movements
-- Motor: PostgreSQL 15
-- ================================================================
-- Mapeo Entidad → Tabla:
--   Persona.java        → personas       (clase base, InheritanceType.JOINED)
--   Cliente.java         → clientes       (hereda de Persona, PK compartida)
--   Cuenta.java          → cuentas        (FK a clientes vía persona_id)
--   Movimiento.java      → movimientos    (FK a cuentas vía cuenta_id)
--   TipoCuenta.java      → ENUM: AHORROS | CORRIENTE
--   TipoMovimiento.java  → ENUM: CREDITO | DEBITO
-- ================================================================

-- =====================
-- 1. CREACIÓN DE TABLAS
-- =====================

-- -----------------------------------------
-- Tabla: personas (Entidad base - Persona)
-- Campos: id, nombre, genero, edad, identificacion, estado
-- -----------------------------------------
CREATE TABLE IF NOT EXISTS personas (
    id              BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(100)    NOT NULL,
    genero          VARCHAR(20),
    edad            INTEGER         CHECK (edad >= 0),
    identificacion  VARCHAR(20)     NOT NULL UNIQUE,
    estado          BOOLEAN         NOT NULL DEFAULT TRUE
);

COMMENT ON TABLE  personas                  IS 'Tabla base para la herencia JOINED. Contiene datos comunes de toda persona.';
COMMENT ON COLUMN personas.id               IS 'PK autoincremental. Compartida con la tabla clientes.';
COMMENT ON COLUMN personas.identificacion   IS 'Cédula o documento único de identidad.';
COMMENT ON COLUMN personas.estado           IS 'TRUE = activo, FALSE = inactivo (borrado lógico).';

-- -----------------------------------------
-- Tabla: clientes (Hereda de Persona)
-- Campos: persona_id (PK/FK), cliente_id, direccion, telefono, password
-- Relación: 1:1 con personas (JOINED inheritance)
-- -----------------------------------------
CREATE TABLE IF NOT EXISTS clientes (
    persona_id  BIGINT          PRIMARY KEY REFERENCES personas(id) ON DELETE CASCADE,
    cliente_id  VARCHAR(50)     NOT NULL UNIQUE,
    direccion   VARCHAR(200),
    telefono    VARCHAR(20),
    password    VARCHAR(255)    NOT NULL
);

COMMENT ON TABLE  clientes              IS 'Extensión de personas con datos específicos de cliente bancario.';
COMMENT ON COLUMN clientes.persona_id   IS 'PK y FK a personas.id (herencia JOINED).';
COMMENT ON COLUMN clientes.cliente_id   IS 'Código único de cliente generado (ej. CLI-0001).';
COMMENT ON COLUMN clientes.password     IS 'Contraseña del cliente.';

-- -----------------------------------------
-- Tabla: cuentas (Entidad Cuenta)
-- Campos: cuenta_id, numero_cuenta, tipo_cuenta, saldo_inicial, saldo, estado, cliente_id
-- Relación: N:1 con clientes
-- -----------------------------------------
CREATE TABLE IF NOT EXISTS cuentas (
    cuenta_id       BIGSERIAL       PRIMARY KEY,
    numero_cuenta   VARCHAR(20)     NOT NULL UNIQUE,
    tipo_cuenta     VARCHAR(20)     NOT NULL CHECK (tipo_cuenta IN ('AHORROS', 'CORRIENTE')),
    saldo_inicial   NUMERIC(15, 2)  NOT NULL DEFAULT 0.00 CHECK (saldo_inicial >= 0),
    saldo           NUMERIC(15, 2)  NOT NULL DEFAULT 0.00,
    estado          BOOLEAN         NOT NULL DEFAULT TRUE,
    cliente_id      BIGINT          NOT NULL REFERENCES personas(id) ON DELETE CASCADE
);

COMMENT ON TABLE  cuentas                   IS 'Cuentas bancarias asociadas a un cliente.';
COMMENT ON COLUMN cuentas.cuenta_id         IS 'PK autoincremental de la cuenta.';
COMMENT ON COLUMN cuentas.numero_cuenta     IS 'Número de cuenta único visible al usuario.';
COMMENT ON COLUMN cuentas.tipo_cuenta       IS 'Tipo de cuenta: AHORROS o CORRIENTE.';
COMMENT ON COLUMN cuentas.saldo_inicial     IS 'Saldo con el que se aperturó la cuenta.';
COMMENT ON COLUMN cuentas.saldo             IS 'Saldo actual calculado tras cada movimiento.';
COMMENT ON COLUMN cuentas.cliente_id        IS 'FK a personas.id (cliente propietario de la cuenta).';

-- -----------------------------------------
-- Tabla: movimientos (Entidad Movimiento)
-- Campos: movimiento_id, fecha, tipo_movimiento, valor, saldo_disponible, estado, cuenta_id
-- Relación: N:1 con cuentas
-- -----------------------------------------
CREATE TABLE IF NOT EXISTS movimientos (
    movimiento_id       BIGSERIAL       PRIMARY KEY,
    fecha               TIMESTAMP       NOT NULL DEFAULT NOW(),
    tipo_movimiento     VARCHAR(20)     NOT NULL CHECK (tipo_movimiento IN ('CREDITO', 'DEBITO')),
    valor               NUMERIC(15, 2)  NOT NULL CHECK (valor > 0),
    saldo_disponible    NUMERIC(15, 2)  NOT NULL,
    estado              BOOLEAN         NOT NULL DEFAULT TRUE,
    cuenta_id           BIGINT          NOT NULL REFERENCES cuentas(cuenta_id) ON DELETE CASCADE
);

COMMENT ON TABLE  movimientos                       IS 'Registro de transacciones bancarias (créditos y débitos).';
COMMENT ON COLUMN movimientos.movimiento_id         IS 'PK autoincremental del movimiento.';
COMMENT ON COLUMN movimientos.fecha                 IS 'Fecha y hora en que se realizó la transacción.';
COMMENT ON COLUMN movimientos.tipo_movimiento       IS 'CREDITO = depósito / ingreso. DEBITO = retiro / egreso.';
COMMENT ON COLUMN movimientos.valor                 IS 'Monto de la transacción (siempre positivo, el signo se infiere del tipo).';
COMMENT ON COLUMN movimientos.saldo_disponible      IS 'Saldo resultante en la cuenta después de aplicar este movimiento.';
COMMENT ON COLUMN movimientos.cuenta_id             IS 'FK a cuentas.cuenta_id (cuenta afectada).';

-- ====================
-- 2. ÍNDICES
-- ====================

CREATE INDEX IF NOT EXISTS idx_movimientos_fecha    ON movimientos(fecha);
CREATE INDEX IF NOT EXISTS idx_movimientos_cuenta   ON movimientos(cuenta_id);
CREATE INDEX IF NOT EXISTS idx_cuentas_cliente      ON cuentas(cliente_id);
CREATE INDEX IF NOT EXISTS idx_personas_identif     ON personas(identificacion);

-- ================================================================
-- 3. DATOS INICIALES DE EJEMPLO
-- ================================================================
-- Estos datos permiten probar los endpoints sin necesidad de crear
-- entidades manualmente. Corresponden a los casos de uso del assessment.
-- ================================================================

-- 3.1 Personas (tabla base)
INSERT INTO personas (nombre, genero, edad, identificacion, estado)
VALUES
    ('Jose Lema',           'Masculino', 30, '1234567890', true),
    ('Marianela Montalvo',  'Femenino',  28, '0987654321', true),
    ('Juan Osorio',         'Masculino', 35, '1122334455', true)
ON CONFLICT (identificacion) DO NOTHING;

-- 3.2 Clientes (extensión de personas)
INSERT INTO clientes (persona_id, cliente_id, direccion, telefono, password)
SELECT id, 'CLI-' || LPAD(id::text, 4, '0'), 'Otavalo sn y principal', '098254785', 'clave123'
FROM personas WHERE identificacion = '1234567890'
ON CONFLICT (persona_id) DO NOTHING;

INSERT INTO clientes (persona_id, cliente_id, direccion, telefono, password)
SELECT id, 'CLI-' || LPAD(id::text, 4, '0'), 'Amazonas y NNUU', '097548965', 'clave456'
FROM personas WHERE identificacion = '0987654321'
ON CONFLICT (persona_id) DO NOTHING;

INSERT INTO clientes (persona_id, cliente_id, direccion, telefono, password)
SELECT id, 'CLI-' || LPAD(id::text, 4, '0'), '13 junio y Equinoccial', '098874587', 'clave789'
FROM personas WHERE identificacion = '1122334455'
ON CONFLICT (persona_id) DO NOTHING;

-- 3.3 Cuentas bancarias
INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo, estado, cliente_id)
SELECT '478758', 'AHORROS', 2000.00, 2000.00, true, id
FROM personas WHERE identificacion = '1234567890'
ON CONFLICT (numero_cuenta) DO NOTHING;

INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo, estado, cliente_id)
SELECT '225487', 'CORRIENTE', 100.00, 100.00, true, id
FROM personas WHERE identificacion = '0987654321'
ON CONFLICT (numero_cuenta) DO NOTHING;

INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo, estado, cliente_id)
SELECT '495878', 'AHORROS', 0.00, 0.00, true, id
FROM personas WHERE identificacion = '1122334455'
ON CONFLICT (numero_cuenta) DO NOTHING;

INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo, estado, cliente_id)
SELECT '496825', 'AHORROS', 540.00, 540.00, true, id
FROM personas WHERE identificacion = '0987654321'
ON CONFLICT (numero_cuenta) DO NOTHING;

-- ================================================================
-- 4. DIAGRAMA DE RELACIONES (Referencia)
-- ================================================================
--
--  ┌──────────────┐       ┌──────────────┐
--  │   personas   │       │   clientes   │
--  │──────────────│       │──────────────│
--  │ id (PK)      │◄──────│ persona_id   │  1:1 JOINED
--  │ nombre       │       │ cliente_id   │
--  │ genero       │       │ direccion    │
--  │ edad         │       │ telefono     │
--  │ identificacion│      │ password     │
--  │ estado       │       └──────────────┘
--  └──────┬───────┘
--         │
--         │ 1:N
--         ▼
--  ┌──────────────┐
--  │   cuentas    │
--  │──────────────│
--  │ cuenta_id(PK)│
--  │ numero_cuenta│       ┌──────────────────┐
--  │ tipo_cuenta  │       │   movimientos    │
--  │ saldo_inicial│       │──────────────────│
--  │ saldo        │◄──────│ cuenta_id (FK)   │  1:N
--  │ estado       │       │ movimiento_id(PK)│
--  │ cliente_id(FK)│      │ fecha            │
--  └──────────────┘       │ tipo_movimiento  │
--                         │ valor            │
--                         │ saldo_disponible │
--                         │ estado           │
--                         └──────────────────┘
-- ================================================================
