package com.starkgrid.demeter.Model.Entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "registry",
        indexes = {
                @Index(name = "idx_registry_path", columnList = "absolutePath"),
                @Index(name = "idx_registry_hash", columnList = "hashSha256")
        }
)
public class RegistryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2048)
    private String absolutePath;

    @Column(nullable = false, length = 512)
    private String fileName;

    @Column(nullable = false)
    private long sizeBytes;

    @Column(nullable = false, length = 128)
    private String hashSha256;

    @Column(nullable = false, length = 256)
    private String mimeType;

    // Simple JSON string for now; Hermes will fill this later
    @Column(columnDefinition = "TEXT")
    private String tagsJson;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    // JPA needs a no-arg constructor
    protected RegistryEntity() {}

    public RegistryEntity(
            String absolutePath,
            String fileName,
            long sizeBytes,
            String hashSha256,
            String mimeType,
            String tagsJson,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.absolutePath = absolutePath;
        this.fileName = fileName;
        this.sizeBytes = sizeBytes;
        this.hashSha256 = hashSha256;
        this.mimeType = mimeType;
        this.tagsJson = tagsJson;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
