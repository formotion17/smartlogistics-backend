package com.enterprise.user.infrastructure.adapter.output.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad de persistencia que mapea la tabla histórica 'user_aud'.
 * <p>
 * Actúa como un libro contable (Ledger) inmutable. Cada registro representa una 
 * instantánea exacta del estado de un usuario en el momento de realizarse una acción.
 * </p>
 */
@Entity
@Table(name = "user_aud")
public class UserAudEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Se mapea con el tipo BIGSERIAL de PostgreSQL
    @Column(name = "audit_id")
    private Long auditId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String status;

    @Column(name = "phone")
    private String phone;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "action_type", nullable = false)
    private String actionType; // 'INSERT', 'UPDATE' o 'DELETE'

    @Column(name = "changed_by", nullable = false)
    private String changedBy; // Operador extraído del contexto de seguridad JWT

    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public UserAudEntity() {
    }

    /**
     * Constructor maestro de inicialización para facilitar la clonación de estados.
     */
    public UserAudEntity(UUID userId, String name, String email, String status, String phone, 
                         boolean active, String actionType, String changedBy, LocalDateTime changedAt, String ipAddress) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.status = status;
        this.phone = phone;
        this.ipAddress = ipAddress;
        this.active = active;
        this.actionType = actionType;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }

    // ===================================================================
    // GETTERS Y SETTERS
    // ===================================================================

    public Long getAuditId() { return auditId; }
    public void setAuditId(Long auditId) { this.auditId = auditId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}