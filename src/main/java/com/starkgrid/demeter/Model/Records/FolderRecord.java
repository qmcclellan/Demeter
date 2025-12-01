package com.starkgrid.demeter.Model.Records;

import java.nio.file.Path;
import java.time.Instant;

public record FolderRecord (

        Path path,
        String folderName,
        Instant lastScannedAt,
        long totalFiles,
        long totalSizeBytes
){}
