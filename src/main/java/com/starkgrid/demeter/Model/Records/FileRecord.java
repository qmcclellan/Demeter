package com.starkgrid.demeter.Model.Records;

import java.nio.file.Path;
import java.time.Instant;

public record FileRecord(
        Path path,
        String fileName,
        long sizeBytes,
        Instant lastModified,
        String mimeType
) {}
