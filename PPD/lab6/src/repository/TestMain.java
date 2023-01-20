package repository;

import model.Inregistrare;
import model.Plata;

import java.io.IOException;

public class TestMain {
    public static void main(String[] args) throws IOException {
        Inregistrare inregistrare = new Inregistrare("nume", 1, "12/12/12", 1, 2, "12/12/12", 12);
        Inregistrare inregistrare1 = new Inregistrare("nume", 12, "12/12/12", 1, 2, "12/12/12", 32);
        Inregistrare inregistrare2 = new Inregistrare("nume", 123, "12/12/12", 1, 2, "12/12/12", 32);
        Plata plata = new Plata("nume", 1, 120, 1);

        Repository repository = new Repository();

        repository.addInregistrare(inregistrare);
        repository.addInregistrare(inregistrare1);
        repository.addInregistrare(inregistrare);
        repository.addPlata(plata);
        repository.addPlata(plata);
        repository.addPlata(plata);
        repository.deleteInregistrare(inregistrare1);
        repository.addInregistrare(inregistrare2);
        repository.addInregistrare(inregistrare1);
        repository.deleteInregistrare(inregistrare2);

        repository.findAllInregistrari().forEach(System.out::println);
        repository.findAllPlati().forEach(System.out::println);


        repository.closeBufferedWriterRepository();

    }
}
