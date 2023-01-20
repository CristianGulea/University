import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import java.util.List;

public class CompanyRepository implements ICompanyRepository {
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
    public void add(Company elem) {
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
    public void delete(Company elem) {
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
    public void update(Company elem, Integer id) {
        initialize();
        try(Session session = sessionFactory.openSession()){
            Transaction tx = null;
            try{
                tx = session.beginTransaction();
                Company company = session.load(Company.class, id);
                company.setId(elem.getId());
                company.setName(elem.getName());
                company.setAddress(elem.getAddress());
                company.setTelephone(elem.getTelephone());
                session.update(company);
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
    public Company findById(Integer id) {
        initialize();
        try (Session session = sessionFactory.openSession()) {
            Company result = null;
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String hql = "from Company where id = :id";
                Query<Company> query = session.createQuery(hql, Company.class);
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
    public Iterable<Company> findAll() {
        initialize();
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            List<Company> result = null;
            try {
                tx = session.beginTransaction();
                String hql = "from Company";
                Query<Company> query = session.createQuery(hql, Company.class);
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
}
