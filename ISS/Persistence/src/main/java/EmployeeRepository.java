import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import java.util.List;

public class EmployeeRepository implements IEmployeeRepository{
    private static SessionFactory sessionFactory;

    static void initialize() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            System.err.println("Exception "+e);
            StandardServiceRegistryBuilder.destroy( registry );
        }
    }

    static void close() {
        if ( sessionFactory != null ) {
            sessionFactory.close();
        }
    }

    @Override
    public void add(Employee elem) {
        initialize();
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.save(elem);
                tx.commit();
            } catch (RuntimeException ex) {
                System.err.println("Eroare la add " + ex);
                if (tx != null)
                    tx.rollback();
            }
        }
        close();
    }

    @Override
    public void delete(Employee elem) {
        initialize();
        try(Session session = sessionFactory.openSession()){
            Transaction tx = null;
            try{
                tx = session.beginTransaction();
                session.delete(elem);
                tx.commit();
            }catch (RuntimeException ex) {
                System.err.println("Eroare la delete "+ex);
                if (tx != null)
                    tx.rollback();
            }
        }
        close();
    }

    @Override
    public void update(Employee elem, Integer id) {
        initialize();
        try(Session session = sessionFactory.openSession()){
            Transaction tx = null;
            try{
                tx = session.beginTransaction();
                Employee employee = session.load(Employee.class, id);
                employee.setId(elem.getId());
                employee.setFirstName(elem.getFirstName());
                employee.setIsAdmin(elem.getIsAdmin());
                employee.setLastName(elem.getLastName());
                employee.setPassword(elem.getPassword());
                employee.setUsername(elem.getUsername());
                session.update(employee);
                tx.commit();
            }catch(RuntimeException ex){
                System.err.println("Eroare la update "+ex);
                if (tx!=null)
                    tx.rollback();
            }
        }
        close();
    }

    @Override
    public Employee findById(Integer id) {
        initialize();
        try (Session session = sessionFactory.openSession()) {
            Employee result = null;
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String hql = "from Employee where id = :id";
                Query<Employee> query = session.createQuery(hql, Employee.class);
                query.setParameter("id", id);
                result = query.uniqueResult();
                tx.commit();
            } catch (RuntimeException ex) {
                System.err.println("Eroare la findById " + ex);
                if (tx != null)
                    tx.rollback();
            }
            close();
            return result;
        }
    }

    @Override
    public Iterable<Employee> findAll() {
        initialize();
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            List<Employee> result = null;
            try {
                tx = session.beginTransaction();
                String hql = "from Employee";
                Query<Employee> query = session.createQuery(hql, Employee.class);
                result = query.list();
                tx.commit();
            }catch (RuntimeException ex) {
                System.err.println("Eroare la findAll " + ex);
                if (tx != null)
                    tx.rollback();
            }
            close();
            return result;
        }
    }

    @Override
    public Employee findByUsername(String username) {
        initialize();
        try (Session session = sessionFactory.openSession()) {
            Employee result = null;
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String hql = "from Employee where username = :username";
                Query<Employee> query = session.createQuery(hql, Employee.class);
                query.setParameter("username", username);
                result = query.uniqueResult();
                tx.commit();
            } catch (RuntimeException ex) {
                System.err.println("Eroare la findById " + ex);
                if (tx != null)
                    tx.rollback();
            }
            close();
            return result;
        }
    }
}
