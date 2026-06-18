-- ===================================================================
-- MIGRACIÓN FLYWAY: V7__add_ip_address_to_user_aud.sql
-- PROPÓSITO: Añadir soporte para trazabilidad de direcciones IP (IPv4/IPv6)
-- ===================================================================

ALTER TABLE user_aud ADD COLUMN ip_address VARCHAR(45);