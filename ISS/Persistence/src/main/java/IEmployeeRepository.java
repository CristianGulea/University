public interface IEmployeeRepository  extends IRepository<Employee, Integer> {
    public Employee findByUsername(String username);
}
