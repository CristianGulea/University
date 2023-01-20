import java.net.Socket;

public class ConcurrentServer extends AbsConcurrentServer {
    private IService chatServer;

    public ConcurrentServer(int port, IService chatServer) {
        super(port);
        this.chatServer = chatServer;
        System.out.println("Chat- ChatRpcConcurrentServer");
    }

    protected Thread createWorker(Socket client) {
        RPCRefectionWorker worker = new RPCRefectionWorker(this.chatServer, client);
        Thread tw = new Thread(worker);
        return tw;
    }

    public void stop() {
        System.out.println("Stopping services ...");
    }
}
