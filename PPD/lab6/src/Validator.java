import jdk.dynalink.linker.LinkerServices;
import model.Inregistrare;
import model.Plata;
import model.Tratament;
import repository.Repository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Validator implements Runnable{

    private Repository repository;
    private int[][] matrixMaxTreatments;
    private List<Inregistrare> inregistrareList;
    private List<Plata> plataList;
    private List<Tratament> treatments;
    private Object lock;
    private Boolean isRunning;



    public Validator(Repository repository, int[][] matrixMaxTreatments, List<Tratament> treatments, Object lock) {
        this.repository = repository;
        this.matrixMaxTreatments = matrixMaxTreatments;
        this.treatments = treatments;
        this.lock = lock;
        this.isRunning = true;
    }

    public void closeIsRunningFromValidator(){
        this.isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (lock) {
                try {
                    this.inregistrareList = repository.findAllInregistrari();
                    this.plataList = repository.findAllPlati();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                List<Inregistrare> unpayed = new ArrayList<>();
                int[] sums = {0, 0, 0, 0, 0};
                int[] sumsFromPlataFile = {0, 0, 0, 0, 0};
                boolean isOverlay = false;

                repository.writeToIstoric("--------------------------------------------------------");

                for (Plata plata : plataList) {
                    sums[plata.getLocatie() - 1] += plata.getSuma();
                }

                for (Inregistrare inregistrare : inregistrareList) {
                    long count = plataList.stream().filter(plata -> plata.getLocatie() == inregistrare.getLocatie() && plata.getCnp() == inregistrare.getCnp() && Integer.parseInt(plata.getData()) == inregistrare.getOra_tratament()).count();
                    if (count == 0) {
                        unpayed.add(inregistrare);
                    }
                    if (!validateNumberOfRecords(inregistrare.getLocatie(), inregistrare.getTip_tratament(), inregistrare.getOra_tratament())) {
                        isOverlay = true;
                    }
                }

                if (isOverlay){
                    repository.writeToIstoric("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!ExistaSuprapuneri!!!!!!!!!!!!!!!!!!!");
                }

                repository.writeToIstoric("Ora verificarii: " + LocalDateTime.now());
                for (int i = 0; i < Data.n; i++) {
                    repository.writeToIstoric("Locatia: " + (i + 1) + " Sold Total: " + sums[i]);
                    for (Inregistrare unpayedInre : unpayed) {
                        if (unpayedInre.getLocatie() == i + 1) {
                            repository.writeToIstoric("Unpayed Record: " + unpayedInre.toString());
                        }
                    }

                    for (int j = 1; j <= Data.m; j++) {
                        for (int minute = 0; minute <= 480; minute += getTreatmentById(j).getDurata()) {
                            repository.writeToIstoric("Tip Tratamanet: " + j + " | Numar maxim admis: " +  matrixMaxTreatments[i][j - 1] + " |  Numar programari: " + getNumberOfTreatments(i + 1, j, minute, minute + getTreatmentById(j).getDurata()) + " |  Calup orar: " + minute + " -> " + (minute + getTreatmentById(j).getDurata()));
                        }
                    }
                }
            }
        }
    }


    private Boolean validateNumberOfRecords(int locatie, int tipTratament, int oraTratament){
        int maxTreatments = matrixMaxTreatments[locatie - 1][tipTratament - 1];
        int durata = getTreatmentById(tipTratament).getDurata();
        List<Inregistrare> records = inregistrareList.stream()
                .filter(record -> record.getLocatie() == locatie)
                .filter(record -> record.getTip_tratament() == tipTratament).toList();

        long count = records.stream().filter(record -> (record.getOra_tratament() < oraTratament && record.getOra_tratament() + durata > oraTratament) ||
                        (record.getOra_tratament() < oraTratament + durata && record.getOra_tratament() + durata > oraTratament + durata) ||
                        (record.getOra_tratament() < oraTratament && record.getOra_tratament() + durata > oraTratament + durata) ||
                        (record.getOra_tratament() > oraTratament && record.getOra_tratament() + durata < oraTratament + durata))
                .count();

        if (count  <= maxTreatments){
            return true;
        }

        records = records.stream().sorted(Comparator.comparing(Inregistrare::getOra_tratament)).collect(Collectors.toList());
        int maxTreatmentsInRealTime = 0;
        List<Inregistrare> activeRecords = new ArrayList<>();
        for (Inregistrare record: records){
            activeRecords.removeIf(activeRecord -> activeRecord.getOra_tratament()  + durata <= record.getOra_tratament());
            activeRecords.add(record);
            if (activeRecords.size() > maxTreatmentsInRealTime){
                maxTreatmentsInRealTime = activeRecords.size();
            }
        }
        if (maxTreatmentsInRealTime > maxTreatments){
            System.out.println("----->>>>>>>>>>" + locatie + "   " + tipTratament + "   " + oraTratament);
        }
        return maxTreatmentsInRealTime <= maxTreatments;
    }

    private long getNumberOfTreatments(int locatie, int tipTratament, int oraTratamentStart, int oraTratamentStop){
        return inregistrareList.stream()
                .filter(record -> record.getLocatie() == locatie)
                .filter(record -> record.getTip_tratament() == tipTratament)
                .filter(record -> record.getOra_tratament() + getTreatmentById(record.getTip_tratament()).getDurata() >= oraTratamentStart && record.getOra_tratament() + getTreatmentById(record.getTip_tratament()).getDurata() <= oraTratamentStop)
                .count();
    }

    private Tratament getTreatmentById(int id){
        return treatments.stream().filter(treatment -> treatment.getTip() == id).findFirst().get();
    }

}
