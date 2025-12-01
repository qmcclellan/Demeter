package com.starkgrid.demeter.Watcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Service
public class RecursiveDirectoryWatcher {

    private static final Logger log = LoggerFactory.getLogger(RecursiveDirectoryWatcher.class);

    private final ExecutorService virtualExecutor;
    private final WatchEventHandler handler;

    private final Map<WatchKey, Path> keyDirectoryMap = new ConcurrentHashMap<>();

    public RecursiveDirectoryWatcher(ExecutorService virtualExecutor,
                                     WatchEventHandler handler) {
        this.virtualExecutor = virtualExecutor;
        this.handler = handler;
    }

    public void startWatching(Path rootPath) {
        virtualExecutor.submit(() -> {
            try {
                runRecursiveWatcher(rootPath);
            } catch (IOException | InterruptedException e) {
                log.error("Watcher crashed: ", e);
                Thread.currentThread().interrupt();
            }
        });
    }

    private void runRecursiveWatcher(Path rootPath) throws IOException, InterruptedException {
        log.info("📂 Starting recursive watcher at: {}", rootPath);

        WatchService watchService = FileSystems.getDefault().newWatchService();

        // 1️⃣ Register existing folder tree
        registerAll(rootPath, watchService);

        // 2️⃣ Event loop
        while (true) {
            WatchKey key = watchService.take(); // blocked on virtual thread = perfect

            Path parentDir = keyDirectoryMap.get(key);
            if (parentDir == null) {
                log.warn("Unknown WatchKey: {}", key);
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {

                WatchEvent.Kind<?> kind = event.kind();

                // Overflow = events lost (rare but real)
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    log.warn("OVERFLOW detected — potential dropped events.");
                    continue;
                }

                Path relativePath = (Path) event.context();
                Path fullPath = parentDir.resolve(relativePath);

                // 3️⃣ NEW DIRECTORY CREATED → must register it
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    if (Files.isDirectory(fullPath)) {
                        log.info("📁 New directory detected — registering recursively: {}", fullPath);
                        registerAll(fullPath, watchService);
                    }
                }

                // 4️⃣ Forward to handler (virtual thread)
                handler.handleEvent(fullPath, kind);
            }

            boolean valid = key.reset();
            if (!valid) {
                keyDirectoryMap.remove(key);
                if (keyDirectoryMap.isEmpty()) {
                    log.warn("No directories left to watch — stopping watcher.");
                    break;
                }
            }
        }
    }

    /**
     * Register a directory AND all its subdirectories.
     */
    private void registerAll(Path start, WatchService watchService) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                registerDirectory(dir, watchService);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void registerDirectory(Path dir, WatchService watchService) throws IOException {
        WatchKey key = dir.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE
        );

        keyDirectoryMap.put(key, dir);
        log.info("🔍 Registered directory: {}", dir);
    }
}
