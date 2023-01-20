import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.ReferenceQueue;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.net.Socket;

public class RPCProxy implements IService{

    private String host;
    private int port;
    private IObserver client;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;
    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;
    private Set<IObserver> observers;

    public RPCProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses=new LinkedBlockingQueue<Response>();
        observers = new HashSet<>();
    }

    private void closeConnection(){
        finished = true;
        try{
            input.close();
            output.close();
            connection.close();
            client = null;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void sendRequest(Request request) throws AppException{
        try{
            output.writeObject(request);
            output.flush();
        }catch (IOException e){
            throw new AppException("Error sending object " + e);
        }
    }

    private Response readResponse() throws AppException{
        Response response = null;
        try{
            response = qresponses.take();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return response;
    }

    private void initializeConnection() throws AppException{
        try{
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void startReader(){
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    private void handleUpdate(Response response){
        if (response.type() == ResponseType.UPDATE) {
            Thread thread = new Thread(this::notifyall);
            thread.start();
        }
    }

    private boolean isUpdate(Response response){
        return ResponseType.UPDATE == response.type();
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

    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    Object response = null;
                    synchronized (input)
                    {
                        response=input.readObject();
                    }
                    System.out.println("response received "+response);
                    if (isUpdate((Response)response)){
                        handleUpdate((Response)response);
                    }else{

                        try {
                            qresponses.put((Response)response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Reading error "+e);
                }
            }
        }
    }

    @Override
    public boolean addProduct(String name, String quantity, String pricePerUnit) throws AppException {
        String merge = name + "," + quantity + "," + pricePerUnit;
        Request req = new Request.Builder().type(RequestType.ADD_PRODUCT).data(merge).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            closeConnection();
            throw new AppException(err);
        }
        else{
            return (boolean)response.data();
        }
    }

    @Override
    public List<Product> findAllProducts() throws AppException {
        Request request = new Request.Builder().type(RequestType.FIND_ALL_PRODUCTS).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new AppException(err);
        }
        else{
            Product[] companies = (Product[])response.data();
            return new ArrayList<>(Arrays.asList(companies));
        }
    }

    @Override
    public void deleteProduct(String id) throws AppException {
        Request req = new Request.Builder().type(RequestType.DELETE_PRODUCT).data(id).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new AppException(err);
        }
    }

    @Override
    public Employee findEmployeeByUsername(String username) throws AppException {
        Request req = new Request.Builder().type(RequestType.FIND_EMPLOYEE_BY_USERNAME).data(username).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new AppException(err);
        }
        else{
            return (Employee) response.data();
        }
    }

    @Override
    public boolean logInEmployee(String username, String password) {
        try {
            initializeConnection();
            Employee employee = new Employee(username, password);
            Request req = new Request.Builder().type(RequestType.LOGIN).data(employee).build();
            sendRequest(req);
            Response response=readResponse();

            if (response.type()== ResponseType.OK){
                this.client=client;
                return (boolean) response.data();
            }
            if (response.type()== ResponseType.ERROR){
                String err=response.data().toString();
                closeConnection();
                throw new AppException(err);
            }
            return false;
        } catch (AppException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateProduct(String name, String quantity, String pricePerUnit, String id) throws AppException {
        String merge = id + "," + name + "," + quantity + "," + pricePerUnit;
        Request req = new Request.Builder().type(RequestType.UPDATE_PRODUCT).data(merge).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            closeConnection();
            throw new AppException(err);
        }
        else{
            return (boolean)response.data();
        }
    }

    @Override
    public List<Torder> findAllOrders() throws AppException {
        Request request = new Request.Builder().type(RequestType.FIND_ALL_ORDERS).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            closeConnection();
            throw new AppException(err);
        }
        else{
            Torder[] torders = (Torder[])response.data();
            return new ArrayList<>(Arrays.asList(torders));
        }
    }

    @Override
    public Product findByProductId(int id) throws AppException {
        Request req = new Request.Builder().type(RequestType.FIND_BY_PRODUCT_ID).data(id).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new AppException(err);
        }
        else{
            return (Product) response.data();
        }
    }

    @Override
    public Torder findByOrderId(int id) throws AppException {
        Request req = new Request.Builder().type(RequestType.FIND_BY_ORDER_ID).data(id).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new AppException(err);
        }
        else{
            return (Torder) response.data();
        }
    }

    @Override
    public void updateOrder(Torder torder) throws AppException {
        Request request = new Request.Builder().type(RequestType.UPDATE_ORDER).data(torder).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new AppException(err);
        }
    }

    @Override
    public List<Company> findAllCompanies() throws AppException {
        Request request = new Request.Builder().type(RequestType.FIND_ALL_COMPANIES).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new AppException(err);
        }
        else{
            Company[] companies = (Company[])response.data();
            return new ArrayList<>(Arrays.asList(companies));
        }
    }

    @Override
    public boolean addOrder(String comments, String price, String status, String companyId, String productId, String quantity) throws AppException {
        String merge = comments + "," + price + "," + status + "," + companyId + "," + productId + "," + quantity;
        Request request = new Request.Builder().type(RequestType.ADD_ORDER).data(merge).build();
        sendRequest(request);
        Response response =  readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new AppException(err);
        }
        return (boolean) response.data();
    }

    @Override
    public void updateOrderStatus(Torder torder) throws AppException {
        Request request = new Request.Builder().type(RequestType.UPDATE_ORDER_STATUS).data(torder).build();
        sendRequest(request);
        Response response =  readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new AppException(err);
        }
    }

}

