package com.starkgrid.demeter.Watcher;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FileWatcherService {

    private static final Logger log = LoggerFactory.getLogger(FileWatcherService.class);

    private final ExecutorService virtualExecutor;
    private final WatchEventHandler handler;

    public FileWatcherService(ExecutorService virtualExecutor,
                              WatchEventHandler handler) {
        this.virtualExecutor = virtualExecutor;
        this.handler = handler;
    }

    public void startWatching(Path rootPath) {
        virtualExecutor.submit(() -> runWatcherLoop(rootPath));
    }

    private void runWatcherLoop(Path rootPath) {
        log.info("📂 Starting file watcher at: {}", rootPath);

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {

            rootPath.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY
            );

            while (true) {
                WatchKey key = watchService.take(); // blocking (perfect for virtual threads)

                for (WatchEvent<?> event : key.pollEvents()) {
                    Path changed = rootPath.resolve((Path) event.context());
                    handler.handleEvent(changed, event.kind());
                }

                boolean valid = key.reset();
                if (!valid) {
                    log.warn("Watch key is no longer valid. Exiting watcher.");
                    break;
                }
            }

        } catch (IOException | InterruptedException e) {
            log.error("Watcher loop error: ", e);
            Thread.currentThread().interrupt();
        }
    }
}
