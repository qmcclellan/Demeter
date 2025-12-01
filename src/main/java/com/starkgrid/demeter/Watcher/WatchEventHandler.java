package com.starkgrid.demeter.Watcher;

import com.starkgrid.demeter.Ingestion.IngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.concurrent.ExecutorService;

@Component
public class WatchEventHandler {

    private static final Logger log = LoggerFactory.getLogger(WatchEventHandler.class);

    private final ExecutorService virtualExecutor;
    private final com.starkgrid.demeter.Ingestion.IngestionService ingestionService;

    public WatchEventHandler(ExecutorService virtualExecutor,
                             com.starkgrid.demeter.Ingestion.IngestionService ingestionService) {
        this.virtualExecutor = virtualExecutor;
        this.ingestionService = ingestionService;
    }

    public void handleEvent(Path path, WatchEvent.Kind<?> kind) {
        log.info("📄 File event: {} - {}", kind.name(), path);

        // push ingestion into virtual thread pipeline
        virtualExecutor.submit(() -> ingestionService.processFileEvent(path, kind));
    }
}
