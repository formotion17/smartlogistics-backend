-- ===================================================================
-- MIGRACIÓN FLYWAY: V6__add_auditing_and_history_tables.sql
-- PROPÓSITO: Implementar enfoque híbrido de auditoría (Inline + Histórico)
-- ===================================================================

-- -------------------------------------------------------------------
-- PARTE 1: Completar campos de auditoría inline en la tabla principal
-- -------------------------------------------------------------------

-- Añadimos quién creó el usuario (ponemos valor por defecto para no romper registros viejos de tu Docker)
ALTER TABLE users ADD COLUMN created_by VARCHAR(255) DEFAULT 'SYSTEM' NOT NULL;

-- Añadimos la fecha de última modificación (con zona horaria timestamptz para entornos cloud)
ALTER TABLE users ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL;

-- Añadimos quién hizo la última modificación
ALTER TABLE users ADD COLUMN updated_by VARCHAR(255) DEFAULT 'SYSTEM' NOT NULL;


-- -------------------------------------------------------------------
-- PARTE 2: Crear la tabla de histórico inmutable (user_aud)
-- -------------------------------------------------------------------
CREATE TABLE user_aud (
    audit_id BIGSERIAL PRIMARY KEY,           -- ID propio del registro de auditoría (Secuencial)
    user_id UUID NOT NULL,                     -- Referencia al usuario afectado
    name VARCHAR(255),                         -- Instantánea del nombre en ese momento
    email VARCHAR(255),                        -- Instantánea del email
    status VARCHAR(255),                       -- Instantánea del rol/estado
    phone VARCHAR(255),                        -- Instantánea del teléfono
    active BOOLEAN,                            -- Instantánea del flag de borrado lógico
    
    -- Metadatos de la acción histórica
    action_type VARCHAR(50) NOT NULL,          -- Tipo de operación: 'INSERT', 'UPDATE' o 'DELETE'
    changed_by VARCHAR(255) NOT NULL,          -- Email del operador que ejecutó la acción (vía JWT)
    changed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL -- Momento exacto del cambio
);

-- Creamos un índice en la tabla de auditoría por el campo user_id 
-- Esto hará que si en el futuro quieres buscar "ver el historial completo del usuario X", la query vuele.
CREATE INDEX idx_user_aud_user_id ON user_aud(user_id);