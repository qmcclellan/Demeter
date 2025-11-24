package main.java.com.starkgrid.demeter;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

@Component
public class FileWatcherService {

public enum Directories{

    folder1("/workspace/Angular"), folder2("/workspace/Java/SprinBoot");

    private final String path;

    Directories(String path){
       this.path = path;
    }

    public String getPath(){

        return path;
    }
}

WatchService watchService;

List<String> directories = Directories.values();


public FileWatcherService(WatchService watchService) {
    this.watchService = FileSystems.getDefault().newWatchService();
}

public void registerPath(String directory){

    Path filePath = Paths.get(directory);

    filePath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);


}
  

public void startWatching(){

    for(Directories d : Directories.values()){

        registerPath(Paths.get(d.getPath()));
    }

    watcherLoop();

}

public void watcherLoop(){

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
