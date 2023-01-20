import model.Inregistrare;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.*;


public class Client implements Runnable {

    private final Socket socket;
    private final DataOutputStream dataOutputStream;
    private final DataInputStream dataInputStream;
    private Boolean isRunning = true;
    private final static int period = 2000;
    private int cnp;
    private String nume;
    private ExecutorService executorService;

    Client(String host, Integer port, Integer cnp) throws IOException {
        this.socket = new Socket(host,port);
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.cnp = cnp;
        this.nume = "nume" + cnp;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void run() {

        while (isRunning) {
            try {
                Thread.sleep(period);
                Random random = new Random();
                Inregistrare inregistrare = new Inregistrare(nume, cnp, "12/12/2023", random.nextInt(1, Data.n + 1), random.nextInt(1, Data.m + 1), "12/12/2023", random.nextInt(0,480));
                Future<?> futureRequest = sendRequest(inregistrare);
                String response = (String) futureRequest.get();
                if (response.equals("ACCEPT")){
                    int anulare = random.nextInt(0, 2);
                    System.out.println("ACCEPTAT->" + inregistrare + ":" + anulare);
                    //Thread.sleep(500);
                    this.executorService.submit(sendPayment(inregistrare));
                    if (anulare == 1){
                        this.executorService.submit(sendCancelPayment(inregistrare));
                    }

                }
                if (response.equals("DECLINE")){
                    System.out.println("DECLINE-> " + inregistrare);
                }

            } catch (Exception e) {
                System.out.println("Client in run: " + e.getMessage());
            }

        }
    }

    public Future<String> sendRequest(Inregistrare inregistrare){

        return executorService.submit(() -> {
            String response = "end";
            try {
//                Thread.sleep(2000);
                dataOutputStream.writeUTF(inregistrare.toString());
                dataOutputStream.flush();
                response = dataInputStream.readUTF();
                if (response.equals("end")) {
                    System.out.println("Client " + Thread.currentThread().getId() + " close...");
                    this.isRunning = false;
                    this.dataOutputStream.close();
                    this.dataInputStream.close();
                    this.socket.close();
                    executorService.shutdown();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Client in sendRequest: " + e.getMessage());
            }
            return response;
        });

    }

    public Runnable sendPayment(Inregistrare inregistrare){
        return () -> {
            try {
                String sendPaymentString = inregistrare.getOra_tratament() + "," + this.cnp + "," + inregistrare.getTip_tratament() + "," + inregistrare.getLocatie();
                dataOutputStream.writeUTF(sendPaymentString);
                dataOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    public Runnable sendCancelPayment(Inregistrare inregistrare){
        return () -> {
            try {
                String sendCancelPaymentString = inregistrare.toString() + "," + "cancel";
                dataOutputStream.writeUTF(sendCancelPaymentString);
                dataOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }
}
