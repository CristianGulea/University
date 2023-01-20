import java.util.concurrent.*;

public class StartServer {

    private static final int timeout = 3;
    private static final int nThreads = 11;

    public static void main(String[] args){

        ExecutorService executor = Executors.newSingleThreadExecutor();
        ExecutorService serverExecutor = Executors.newFixedThreadPool(nThreads);
        Server server = new Server(serverExecutor);
        Future<?> future = executor.submit(server);

        try {
            future.get(timeout, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            future.cancel(true);
        } catch (Exception e) {
            System.out.println("StartService in main: " + e.getMessage());
        } finally {
            server.close();
            executor.shutdown();
        }

        serverExecutor.shutdown();
    }
}
