package com.starkgrid.demeter.Records;

import java.time.Instant;

public record DemeterRegistry() {

    String id;
    String fileName;
    String  absolutePath;
    String folderPath;
    long sizeBytes;
    Instant createdAt;
    Instant modifiedAt;
    List<String> tags;
    String vectorId;
        
}