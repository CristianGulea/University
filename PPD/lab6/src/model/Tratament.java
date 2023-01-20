package model;

public class Tratament {
    private int tip;
    private int cost;
    private int durata;

    public Tratament(int tip, int cost, int durata) {
        this.tip = tip;
        this.cost = cost;
        this.durata = durata;
    }

    public int getTip() {
        return tip;
    }

    public void setTip(int tip) {
        this.tip = tip;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getDurata() {
        return durata;
    }

    public void setDurata(int durata) {
        this.durata = durata;
    }

    @Override
    public String toString() {
        return tip + "," + cost + "," + durata;
    }
}
