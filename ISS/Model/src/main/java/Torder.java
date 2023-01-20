import java.io.Serializable;

public class Torder implements Serializable {
    private int id;
    private String comments;
    private int price;
    private String status;
    private int companyId;
    private int productId;
    private int quantity;

    public Torder(String comments, int price, String status, int companyId, int productId, int quantity) {
        this.comments = comments;
        this.price = price;
        this.status = status;
        this.companyId = companyId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public Torder() {
        this.comments = null;
        this.price = -1;
        this.status = null;
        this.companyId = -1;
        this.productId = -1;
        this.quantity = -1;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "Torder{" +
                "id=" + id +
                ", comments='" + comments + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                ", companyId=" + companyId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                '}';
    }
}
