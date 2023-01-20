import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceImplementation implements IService, IObservable{
    private ProductRepository productRepository;
    private EmployeeRepository employeeRepository;
    private TorderRepository torderRepository;
    private CompanyRepository companyRepository;
    Set<IObserver> observers;

    public ServiceImplementation(ProductRepository productRepository, EmployeeRepository employeeRepository, TorderRepository torderRepository, CompanyRepository companyRepository) {
        this.productRepository = productRepository;
        this.employeeRepository = employeeRepository;
        this.torderRepository = torderRepository;
        this.companyRepository = companyRepository;
        observers = new HashSet<>();
    }

    private boolean validateProduct(String name, String quantity, String pricePerUnit){
        try {
            if ((name.isEmpty()) || (Integer.parseInt(quantity) <= 0) || (Integer.parseInt(pricePerUnit) <= 0))
                return false;
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    public boolean addProduct(String name, String quantity, String pricePerUnit) {
        boolean isValid = validateProduct(name, quantity, pricePerUnit);
        if (isValid) {
            Product product = new Product(name, Integer.parseInt(pricePerUnit), Integer.parseInt(quantity));
            productRepository.add(product);
        }
        Thread thread = new Thread(this::notifyall);
        thread.start();
        return isValid;
    }

    @Override
    public List<Product> findAllProducts() {
        return (List<Product>) productRepository.findAll();
    }

    @Override
    public void deleteProduct(String id) {
        Product product = new Product();
        product.setId(Integer.parseInt(id));
        productRepository.delete(product);
        Thread thread = new Thread(this::notifyall);
        thread.start();
    }

    @Override
    public Employee findEmployeeByUsername(String username) {
        return employeeRepository.findByUsername(username);
    }

    @Override
    public boolean logInEmployee(String username, String password) throws AppException {
        Employee employee = employeeRepository.findByUsername(username);
        boolean response = false;
        if (employee != null){
            response = Objects.equals(password, employee.getPassword());
            return response;
        }
        return false;
    }

    @Override
    public boolean updateProduct(String name, String quantity, String pricePerUnit, String id) {
        boolean isValid = validateProduct(name, quantity, pricePerUnit);
        if (isValid) {
            Product product = new Product(name, Integer.parseInt(pricePerUnit), Integer.parseInt(quantity));
            product.setId(Integer.parseInt(id));
            productRepository.update(product, product.getId());
        }
        Thread thread = new Thread(this::notifyall);
        thread.start();
        return isValid;
    }

    @Override
    public List<Torder> findAllOrders() {
        return (List<Torder>) torderRepository.findAll();
    }

    @Override
    public Product findByProductId(int id) {
        return productRepository.findById(id);
    }

    @Override
    public Torder findByOrderId(int id) {
        return torderRepository.findById(id);
    }

    @Override
    public void updateOrder(Torder torder) {
        torderRepository.update(torder, torder.getId());
        Thread thread = new Thread(this::notifyall);
        thread.start();
    }

    @Override
    public List<Company> findAllCompanies() {
        return (List<Company>) companyRepository.findAll();
    }

    private boolean validateOrder(String price, String companyId, String productId, String quantity){
        if (Integer.parseInt(productId) >= 0 && Integer.parseInt(companyId) >= 0 && Integer.parseInt(price) >= 0 && Integer.parseInt(quantity) >= 0){
            Product product = productRepository.findById(Integer.parseInt(productId));
            if (product.getQuantity() < Integer.parseInt(quantity)){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addOrder(String comments, String price, String status, String companyId, String productId, String quantity) {
        if (validateOrder(price, companyId, productId, quantity)) {
            Torder torder = new Torder(comments, Integer.parseInt(price), status, Integer.parseInt(companyId), Integer.parseInt(productId), Integer.parseInt(quantity));
            torderRepository.add(torder);
            Product product = productRepository.findById(Integer.parseInt(productId));
            product.setQuantity(product.getQuantity() - Integer.parseInt(quantity));
            productRepository.update(product, Integer.parseInt(productId));
            Thread thread = new Thread(this::notifyall);
            thread.start();
            return true;
        }
        return false;
    }

    @Override
    public void updateOrderStatus(Torder torder) {
        torder.setStatus("delivered");
        torderRepository.update(torder, torder.getId());
    }

    @Override
    public void addObserver(IObserver observer) {
        observers.add(observer);
    }

    @Override
    public void notifyall() {
        observers.forEach(IObserver::notifyApp);
    }

    @Override
    public void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

}
