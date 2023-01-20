import javax.naming.spi.ResolveResult;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.List;

public class RPCRefectionWorker implements Runnable, IObserver{
    private IService server;
    private Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public RPCRefectionWorker(IService server, Socket connection){
        this.server = server;
        this.connection = connection;
        try{
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            connected=true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Response handleRequest(Request request){
        Response response=null;
        String handlerName="handle"+(request).type();
        System.out.println("HandlerName "+handlerName);
        try {
            Method method=this.getClass().getDeclaredMethod(handlerName, Request.class);
            response=(Response)method.invoke(this,request);
            System.out.println("Method "+handlerName+ " invoked");
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return response;
    }

    private void sendResponse(Response response) throws IOException{
        System.out.println("sending response "+response);
        synchronized (output) {
            output.writeObject(response);
        }
        output.flush();
    }

    @Override
    public void run() {
        while(connected){
            try {
                Object request=input.readObject();
                Response response=handleRequest((Request)request);
                if (response!=null){
                    sendResponse(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error "+e);
        }
    }

    private Response handleLOGIN(Request request) throws AppException {
        System.out.println("Login request ..."+request.type());
        Employee employee=(Employee) request.data();
        boolean rasp = server.logInEmployee(employee.getUsername(), employee.getPassword());
        if (rasp){
            server.addObserver(this);
        }
        return new Response.Builder().type(ResponseType.OK).data(rasp).build();
    }

    private Response handleFIND_EMPLOYEE_BY_USERNAME(Request request){
        try{
            String username = (String) request.data();
            Employee employee = server.findEmployeeByUsername(username);
            return new Response.Builder().type(ResponseType.OK).data(employee).build();
        }
        catch (AppException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleFIND_ALL_COMPANIES(Request request) throws AppException {
        List<Company> companies = server.findAllCompanies();
        Company[] companies1 = new Company[companies.size()];
        for (int i = 0; i<companies.size(); i++){
            companies1[i] = companies.get(i);
        }
        return  new Response.Builder().type(ResponseType.OK).data(companies1).build();
    }

    private Response handleFIND_ALL_PRODUCTS(Request request) throws AppException {
        List<Product> products = server.findAllProducts();
        Product[] products1 = new Product[products.size()];
        for (int i = 0; i<products.size(); i++){
            products1[i] = products.get(i);
        }
        return  new Response.Builder().type(ResponseType.OK).data(products1).build();
    }

    private Response handleDELETE_PRODUCT(Request request){
        String id = (String) request.data();
        try {
            server.deleteProduct(id);
        } catch (AppException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
        return  new Response.Builder().type(ResponseType.OK).build();
    }

    private Response handleADD_PRODUCT(Request request) {
        try {
            String merge = (String) request.data();
            List<String> dates = List.of(merge.split(","));
            boolean val = server.addProduct(dates.get(0), dates.get(1), dates.get(2));
            return new Response.Builder().type(ResponseType.OK).data(val).build();
        } catch (AppException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleUPDATE_PRODUCT(Request request){
        try {
            String merge = (String) request.data();
            List<String> dates = List.of(merge.split(","));
            boolean val = server.updateProduct(dates.get(1), dates.get(2), dates.get(3), dates.get(0));
            return new Response.Builder().type(ResponseType.OK).data(val).build();
        } catch (AppException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleFIND_ALL_ORDERS(Request request){
        try {
            List<Torder> torders = server.findAllOrders();
            Torder[] torders1 = new Torder[torders.size()];
            for (int i = 0; i<torders.size(); i++){
                torders1[i] = torders.get(i);
            }
            return new Response.Builder().type(ResponseType.OK).data(torders1).build();
        }
        catch (AppException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleFIND_BY_PRODUCT_ID(Request request){
        try{
            int id = (int) request.data();
            Product product = server.findByProductId(id);
            return new Response.Builder().type(ResponseType.OK).data(product).build();

        }catch (AppException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleFIND_BY_ORDER_ID(Request request){
        try{
            int id = (int) request.data();
            Torder torder = server.findByOrderId(id);
            return new Response.Builder().type(ResponseType.OK).data(torder).build();

        }catch (AppException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleADD_ORDER(Request request){
        try{
            String merge = (String) request.data();
            List<String> dates = List.of(merge.split(","));
            boolean sem = server.addOrder(dates.get(0), dates.get(1), dates.get(2), dates.get(3), dates.get(4), dates.get(5));
            return new Response.Builder().type(ResponseType.OK).data(sem).build();
        }
        catch (AppException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleUPDATE_ORDER(Request request){
        try{
            Torder torder = (Torder) request.data();
            server.updateOrder(torder);
            return new Response.Builder().type(ResponseType.OK).build();
        }
        catch (AppException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleUPDATE_ORDER_STATUS(Request request){
        try{
            Torder torder = (Torder) request.data();
            server.updateOrderStatus(torder);
            return new Response.Builder().type(ResponseType.OK).build();
        }catch (AppException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    @Override
    public void notifyApp() {
        try {
            sendResponse(new Response.Builder().type(ResponseType.UPDATE).build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
