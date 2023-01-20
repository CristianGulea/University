package model;

public class Inregistrare {
    private String nume;
    private int cnp;
    private String data;
    private int locatie;
    private int tip_tratament;
    private String data_tratament;
    private int ora_tratament;

    public Inregistrare(String nume, int cnp, String data, int locatie, int tip_tratament, String data_tratament, int ora_tratament) {
        this.nume = nume;
        this.cnp = cnp;
        this.data = data;
        this.locatie = locatie;
        this.tip_tratament = tip_tratament;
        this.data_tratament = data_tratament;
        this.ora_tratament = ora_tratament;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public int getCnp() {
        return cnp;
    }

    public void setCnp(int cnp) {
        this.cnp = cnp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getLocatie() {
        return locatie;
    }

    public void setLocatie(int locatie) {
        this.locatie = locatie;
    }

    public int getTip_tratament() {
        return tip_tratament;
    }

    public void setTip_tratament(int tip_tratament) {
        this.tip_tratament = tip_tratament;
    }

    public String getData_tratament() {
        return data_tratament;
    }

    public void setData_tratament(String data_tratament) {
        this.data_tratament = data_tratament;
    }

    public int getOra_tratament() {
        return ora_tratament;
    }

    public void setOra_tratament(int ora_tratament) {
        this.ora_tratament = ora_tratament;
    }

    @Override
    public String toString() {
        return nume + "," + cnp + "," + data + "," + locatie + "," + tip_tratament + "," + data_tratament + "," + ora_tratament;
    }

}
