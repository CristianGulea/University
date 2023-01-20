public class StartRPCServer {

    public static void main(String[] args) {
        EmployeeRepository employeeRepository = new EmployeeRepository();
        ProductRepository productRepository = new ProductRepository();
        TorderRepository torderRepository = new TorderRepository();
        CompanyRepository companyRepository = new CompanyRepository();
        IService service = new ServiceImplementation(productRepository, employeeRepository, torderRepository, companyRepository);

        int defaultPort = 55555;
        AbstractServer server = new ConcurrentServer(defaultPort, service);
        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("Error starting the server" + e.getMessage());
        }finally {
            try {
                server.stop();
            }catch(ServerException e){
                System.err.println("Error stopping server "+e.getMessage());
            }
        }
    }
}
