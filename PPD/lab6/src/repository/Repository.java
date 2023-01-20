package repository;

import model.Inregistrare;
import model.Plata;

import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

public class Repository {
    private String inregistrareFile = "Inreg.txt";
    private String plataFile = "Plata.txt";
    private String istoricFile = "Istoric.txt";
    private BufferedWriter bufferedWriterInreg;
    private BufferedWriter bufferedWriterPlata;
    private BufferedWriter bufferedWriterIstoric;

    public Repository() {
        try {
            this.bufferedWriterInreg = new BufferedWriter(new FileWriter(inregistrareFile));
            this.bufferedWriterPlata = new BufferedWriter(new FileWriter(plataFile));
            this.bufferedWriterIstoric = new BufferedWriter(new FileWriter(istoricFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ;
    }

    public void closeBufferedWriterRepository(){
        try {
            this.bufferedWriterInreg.close();
            this.bufferedWriterPlata.close();
            this.bufferedWriterIstoric.close();
        } catch (IOException e) {
            System.out.println("Repository in closeBufferedWriterRepository: " + e.getMessage());
        }
    }

    public synchronized void addPlata(Plata plata) throws IOException {
        /*if (plata.getSuma() > 0){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
        bufferedWriterPlata.append(plata.toString()).append("\n");
        bufferedWriterPlata.flush();
    }

    public synchronized void addInregistrare(Inregistrare inregistrare) throws IOException {
        bufferedWriterInreg.append(inregistrare.toString()).append("\n");
        bufferedWriterInreg.flush();
    }

    public synchronized void deleteInregistrare(Inregistrare inregistrare) throws IOException {
        File inputFile = new File(inregistrareFile);
        File tempFile = new File("tmp.txt");

        BufferedReader readerInreg = new BufferedReader(new FileReader(inputFile));

        BufferedReader readerTmp = new BufferedReader(new FileReader(tempFile));
        BufferedWriter writerTmp= new BufferedWriter(new FileWriter(tempFile));


        String lineToRemove = inregistrare.toString();
        String currentLine;

        while((currentLine = readerInreg.readLine()) != null) {
            String trimmedLine = currentLine.trim();
            if(trimmedLine.equals(lineToRemove)) continue;
            writerTmp.append(currentLine).append("\n");
        }
        writerTmp.flush();

        this.bufferedWriterInreg.close();
        this.bufferedWriterInreg = new BufferedWriter(new FileWriter(inputFile));

        while((currentLine = readerTmp.readLine()) != null) {
            String trimmedLine = currentLine.trim();
            if(trimmedLine.equals(lineToRemove)) continue;
            bufferedWriterInreg.append(currentLine).append("\n");
        }

        bufferedWriterInreg.flush();
        writerTmp.close();
        readerInreg.close();
        readerTmp.close();
    }

    public synchronized List<Inregistrare> findAllInregistrari() throws IOException {
        List<Inregistrare> records = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(inregistrareFile));
        String line = bufferedReader.readLine();
        while (line != null){
            records.add(new Inregistrare(line.split(",")[0], Integer.parseInt(line.split(",")[1]), line.split(",")[2], Integer.parseInt(line.split(",")[3]), Integer.parseInt(line.split(",")[4]), line.split(",")[5], Integer.parseInt(line.split(",")[6])));
            line = bufferedReader.readLine();
        }
        return records;
    }

    public synchronized List<Plata> findAllPlati() throws IOException {
        List<Plata> payments = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(plataFile));
        String line = bufferedReader.readLine();
        while (line != null){
            payments.add(new Plata(line.split(",")[0], Integer.parseInt(line.split(",")[1]), Integer.parseInt(line.split(",")[2]), Integer.parseInt(line.split(",")[3])));
            line = bufferedReader.readLine();
        }
        return payments;
    }

    public synchronized List<Inregistrare> findAllInregFromAClient(int clientId) throws IOException {
        List<Inregistrare> records = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(inregistrareFile));
        String line = bufferedReader.readLine();
        while (line != null){
            if (Integer.parseInt(line.split(",")[1]) == clientId) {
                records.add(new Inregistrare(line.split(",")[0], Integer.parseInt(line.split(",")[1]), line.split(",")[2], Integer.parseInt(line.split(",")[3]), Integer.parseInt(line.split(",")[4]), line.split(",")[5], Integer.parseInt(line.split(",")[6])));
            }
            line = bufferedReader.readLine();
        }
        return records;
    }

    public synchronized void writeToIstoric(String line){
        try {
            bufferedWriterIstoric.append(line).append("\n");
            bufferedWriterIstoric.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
