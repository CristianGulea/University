import java.util.List;

public interface IService extends IObservable{
    boolean addProduct(String name, String quantity, String pricePerUnit) throws AppException;

    List<Product> findAllProducts() throws AppException;

    void deleteProduct(String id) throws AppException;

    Employee findEmployeeByUsername(String username) throws AppException;

    boolean logInEmployee(String username, String password) throws AppException;

    boolean updateProduct(String name, String quantity, String pricePerUnit, String id) throws AppException;

    List<Torder> findAllOrders() throws AppException;

    Product findByProductId(int id) throws AppException;

    Torder findByOrderId(int id) throws AppException;

    void updateOrder(Torder torder) throws AppException;

    List<Company> findAllCompanies() throws AppException;

    boolean addOrder(String comments, String price, String status, String companyId, String productId, String quantity) throws AppException;

    void updateOrderStatus(Torder torder) throws AppException;
}
