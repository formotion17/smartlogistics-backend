-- V1: Creación inicial de la tabla de usuarios

CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL
);