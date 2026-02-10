package com.example.template.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

/**
 * Base class for all entities requiring auditing.
 * Uses OffsetDateTime for global timezone consistency.
 */
@Getter
@Setter
@MappedSuperclass // Designates a class whose mapping information is inherited by subclass entities. It does not have its own database table.
@EntityListeners(AuditingEntityListener.class) // A JPA Entity Listener that automatically captures auditing information on persist and update operations.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public abstract class AuditableEntity {

    /**
     * Creation timestamp. Set once and never updated.
     * Precision (6) ensures microseconds accuracy in modern DBs (PostgreSQL/MySQL 8+).
     */
    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP(6) WITH TIME ZONE") // Forces the database to use a specific SQL type. Essential for cross-database consistency and microsecond precision.
    protected OffsetDateTime createdAt;

    /**
     * Last modification timestamp. Updated on every change.
     */
    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) WITH TIME ZONE")
    protected OffsetDateTime updatedAt;

    /**
     * Identifier of the user who created the record.
     */
    @CreatedBy
    @Column(updatable = false, length = 100) // 100 is safer for long emails/UUIDs
    protected String createdBy;

    /**
     * Identifier of the user who last modified the record.
     */
    @LastModifiedBy
    @Column(length = 100)
    protected String lastModifiedBy;
}
