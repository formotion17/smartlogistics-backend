-- Añadimos la columna password a la tabla users.
-- Usamos un hash de BCrypt por defecto ('password123') para que no fallen los usuarios de prueba que ya tuvieras guardados en la BD.
ALTER TABLE users ADD COLUMN password VARCHAR(255) NOT NULL DEFAULT '$2a$10$UnX66q7Bf.d29fWUXC1YEuPqO9R0eZ7.jF7L9m0qM2q4F4g6K2yCu';