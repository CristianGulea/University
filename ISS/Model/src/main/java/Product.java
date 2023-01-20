import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String name;
    private int pricePerUnit;
    private int quantity;


    public Product(String name, int pricePerUnit, int quantity) {
        this.name = name;
        this.pricePerUnit = pricePerUnit;
        this.quantity = quantity;
    }

    public Product() {
        this.id = -1;
        this.name = null;
        this.pricePerUnit = -1;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPricePerUnit() {
        return pricePerUnit;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPricePerUnit(int pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }


    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pricePerUnit=" + pricePerUnit +
                ", quantity=" + quantity +
                '}';
    }
}
