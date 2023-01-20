import java.io.IOException;
import java.util.concurrent.*;

public class StartClient {

    private final static int port = 4000;
    private final static String url = "localhost";
    private final static int nThreads = 10;

    public static void main(String[] args) throws IOException {

        ExecutorService exec = Executors.newFixedThreadPool(nThreads);

        for (int i = 0; i < nThreads; i++){
            exec.execute(new Client(url, port, i));
        }

        exec.shutdown();
    }
}
