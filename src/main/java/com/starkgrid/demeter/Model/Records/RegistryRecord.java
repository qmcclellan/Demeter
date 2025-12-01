package com.starkgrid.demeter.Model.Records;

import java.time.Instant;
import java.util.List;

public record RegistryRecord(
        Long id,                 // null before DB insert
        String absolutePath,
        String fileName,
        long sizeBytes,
        String hashSha256,
        String mimeType,
        List<String> tags,       // Hermes will fill these later
        Instant createdAt,
        Instant updatedAt
) {}