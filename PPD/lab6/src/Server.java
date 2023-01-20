import model.Inregistrare;
import model.Plata;
import model.Tratament;
import repository.Repository;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable{

    private Executor exec;
    private boolean isRunning = true;
    private boolean runServer = true;
    private List<Socket> clients = new ArrayList<>();
    private ServerSocket socket;
    private final int nThreads = 10;
    private final int port = 4000;
    private List<Tratament> treatments;
    private Repository repository;
    private int[][] matrixMaxTreatments;
    private final  static Object lock = new Object();
    private Validator validator;

    public Server(ExecutorService exec){
        this.exec = exec;
        try {
            this.socket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.treatments = initTreatments();
        this.matrixMaxTreatments = initMaxTreatments();
        this.repository = new Repository();
        this.validator = new Validator(repository, this.matrixMaxTreatments, this.treatments, this.lock);
        this.exec.execute(validator);
    }

    private List<Tratament> initTreatments(){
        List<Tratament> treatments = new ArrayList<>();
        treatments.add(new Tratament(1, 50, 120));
        treatments.add(new Tratament(2, 20, 20));
        treatments.add(new Tratament(3, 40, 30));
        treatments.add(new Tratament(4, 100, 60));
        treatments.add(new Tratament(5, 30, 30));
        return treatments;
    }

    private int[][] initMaxTreatments(){
        int[][] matrix = new int[Data.n][Data.m];
        matrix[0][0] = 3; matrix[0][1] = 1; matrix[0][2] = 1; matrix[0][3] = 2; matrix[0][4] = 1;
        for (int i = 1; i < Data.n; i++){
            for (int j = 0; j < Data.m; j++){
                //N(i,j) =N(1,j)*(i-1) -> (i-1) la noi (i+1-1)
                matrix[i][j] = matrix[0][j] * i;
            }
        }
        return matrix;
    }

    @Override
    public void run() {
        System.out.println("Server start...");
        Object oLock = new Object();
        while (runServer) {
            try {
                final Socket connection = socket.accept();
                System.out.println("Server accept connection...");
                clients.add(connection);
                exec.execute(handleRequest(connection, oLock));
            } catch (IOException e) {
                System.out.println("Server in run: " + e.getMessage());
            }
        }
        System.out.println("Server finish...");
    }



    private Runnable handleRequest(Socket connection, Object oLock){
        return () -> {
            try {
                DataInputStream dataInputStream = new DataInputStream(connection.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                String input = "";
                while (isRunning){
                    if (isRunning) {
                        input = dataInputStream.readUTF();
                        if (input.split(",").length == 7) {
                            //System.out.println("Server: " + Thread.currentThread().getId() + " -> " + input);
                            synchronized (oLock) {
                                Inregistrare receiveRecord = new Inregistrare(input.split(",")[0], Integer.parseInt(input.split(",")[1]), input.split(",")[2], Integer.parseInt(input.split(",")[3]), Integer.parseInt(input.split(",")[4]), input.split(",")[5], Integer.parseInt(input.split(",")[6]));
                                Boolean isRecordValid = validateRecord(receiveRecord);
                                if (isRecordValid) {
                                    dataOutputStream.writeUTF("ACCEPT");
                                    repository.addInregistrare(receiveRecord);
                                } else {
                                    dataOutputStream.writeUTF("DECLINE");
                                }
                            }
                        }
                        else if (input.split(",").length == 4) {
                            System.out.println("Server: " + Thread.currentThread().getId() + " -> " + input);
                            //Thread.sleep(500);
                            Plata plata = new Plata(input.split(",")[0], Integer.parseInt(input.split(",")[1]), getTreatmentById(Integer.parseInt(input.split(",")[2])).getCost(), Integer.parseInt(input.split(",")[3]));
                            repository.addPlata(plata);
                        }
                        else if (input.split(",").length == 8) {
                            synchronized (lock) {
                                System.out.println("Server: " + Thread.currentThread().getId() + " -> " + input + ": cancel");
                                Inregistrare receiveRecord = new Inregistrare(input.split(",")[0], Integer.parseInt(input.split(",")[1]), input.split(",")[2], Integer.parseInt(input.split(",")[3]), Integer.parseInt(input.split(",")[4]), input.split(",")[5], Integer.parseInt(input.split(",")[6]));
                                synchronized (this) {
                                    repository.deleteInregistrare(receiveRecord);
                                    Plata plata = new Plata(String.valueOf(receiveRecord.getOra_tratament()), receiveRecord.getCnp(), (-1) * getTreatmentById(receiveRecord.getTip_tratament()).getCost(), receiveRecord.getLocatie());
                                    repository.addPlata(plata);
                                }
                            }
                        }
                    }
                }
                dataInputStream.close();
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Server " + Thread.currentThread().getId() + " in task in run: " + e.getMessage());
            }
        };
    }

    private synchronized Boolean validateRecord(Inregistrare receiveRecord) throws IOException {
        if (receiveRecord.getOra_tratament() + getTreatmentById(receiveRecord.getTip_tratament()).getDurata() > 480){
            return false;
        }
//        if (!validateClientRequest(receiveRecord.getCnp(), receiveRecord)){
//            return false;
//        }
        int maxTreatments = matrixMaxTreatments[receiveRecord.getLocatie() - 1][receiveRecord.getTip_tratament() - 1];
        int durata = getTreatmentById(receiveRecord.getTip_tratament()).getDurata();
        List<Inregistrare> records = repository.findAllInregistrari();
        return records.stream()
                .filter(record -> record.getLocatie() == receiveRecord.getLocatie())
                .filter(record -> record.getTip_tratament() == receiveRecord.getTip_tratament())
                .filter(record -> record.getOra_tratament() > receiveRecord.getOra_tratament() - getTreatmentById(receiveRecord.getTip_tratament()).getDurata() && record.getOra_tratament() < receiveRecord.getOra_tratament() + getTreatmentById(receiveRecord.getTip_tratament()).getDurata())
                .count() < maxTreatments;
    }

    //Valideaza daca clientul are sau nu o alta programare in perioada programarii din request
    private Boolean validateClientRequest(int cnp, Inregistrare inregistrare){
        try {
            List<Inregistrare> clientRecords = repository.findAllInregFromAClient(cnp);
            for (Inregistrare record : clientRecords){
                if ((inregistrare.getOra_tratament() <=record.getOra_tratament() && inregistrare.getOra_tratament() + getTreatmentById(inregistrare.getTip_tratament()).getDurata() >=record.getOra_tratament()) || (inregistrare.getOra_tratament() >= record.getOra_tratament() && inregistrare.getOra_tratament() <= record.getOra_tratament() + getTreatmentById(record.getTip_tratament()).getDurata())){
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private Tratament getTreatmentById(int id){
        return treatments.stream().filter(treatment -> treatment.getTip() == id).findFirst().get();
    }


    public void close() {
        try {
            this.clients.forEach(client -> {
                try {
                    System.out.println("Server " + Thread.currentThread().getId() + " close " + client);
                    DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
                    dataOutputStream.writeUTF("end");
                    dataOutputStream.flush();
                    //dataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            this.socket.close();
            this.isRunning = false;
            this.runServer = false;
            this.validator.closeIsRunningFromValidator();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
