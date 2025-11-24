import java.util.concurrent.ExecutorService;

@Component
public class FileExecutor {

ExecutorService executorService;

public FileExecutor(ExecutorService executorService) {
    this.executorService = executorService;
}


    
}
