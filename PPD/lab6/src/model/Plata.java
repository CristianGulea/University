package model;

public class Plata {
    private String data;
    private int cnp;
    private int suma;
    private int locatie;



    public Plata(String data, int cnp, int suma, int locatie) {
        this.data = data;
        this.cnp = cnp;
        this.suma = suma;
        this.locatie = locatie;
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

    public int getCnp() {
        return cnp;
    }

    public void setCnp(int cnp) {
        this.cnp = cnp;
    }

    public int getSuma() {
        return suma;
    }

    public void setSuma(int suma) {
        this.suma = suma;
    }

    @Override
    public String toString() {
        return data + "," + cnp + "," + suma + "," + locatie;
    }
}
