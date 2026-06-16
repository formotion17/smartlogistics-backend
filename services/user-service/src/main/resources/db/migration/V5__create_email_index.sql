-- Creamos un índice B-Tree en la columna email para optimizar el Login.
-- El condicional 'IF NOT EXISTS' evita fallos si se ejecuta el script en otros entornos.
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);