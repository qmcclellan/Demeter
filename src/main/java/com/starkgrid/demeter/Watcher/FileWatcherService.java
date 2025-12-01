package com.starkgrid.demeter.Watcher;

import java.io.IOException;
import java.nio.file.*;
import org.springframework.stereotype.Component;

@Component
public class FileWatcherService {

    public enum Directories {
        FOLDER1("/workspace/Angular"),
        FOLDER2("~/workspace/Java");

        private final String path;

        Directories(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    private final WatchService watchService;

    public FileWatcherService() throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
    }

    public void registerPath(Path path) throws IOException {
        path.register(
            watchService,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_MODIFY,
            StandardWatchEventKinds.ENTRY_DELETE
        );
    }

    public void startWatching() throws IOException, InterruptedException {
        for (Directories d : Directories.values()) {
            registerPath(Paths.get(d.getPath()));
        }

        watcherLoop();
    }

    public void watcherLoop() throws InterruptedException {
        while (true) {
            WatchKey key = watchService.take();

            for (WatchEvent<?> event : key.pollEvents()) {
                Path changed = (Path) event.context();
                System.out.println("EVENT: " + event.kind() + " on " + changed);
            }

            key.reset();
        }
    }
}
