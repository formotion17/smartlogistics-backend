-- Añadimos la columna booleana para controlar el estado del usuario.
-- Usamos 'DEFAULT TRUE' para que todos los usuarios que ya tienes creados 
-- pasen a estar activos automáticamente en la migración.
ALTER TABLE users ADD COLUMN active BOOLEAN DEFAULT TRUE NOT NULL;