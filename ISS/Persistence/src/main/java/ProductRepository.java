import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import java.util.List;

public class ProductRepository implements IProductRepository{
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
    public void add(Product elem) {
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
    public void delete(Product elem) {
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
    public void update(Product elem, Integer id) {
        initialize();
        try(Session session = sessionFactory.openSession()){
            Transaction tx = null;
            try{
                tx = session.beginTransaction();
                Product product = session.load(Product.class, id);
                product.setId(elem.getId());
                product.setName(elem.getName());
                product.setPricePerUnit(elem.getPricePerUnit());
                product.setQuantity(elem.getQuantity());
                session.update(product);
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
    public Product findById(Integer id) {
        initialize();
        try (Session session = sessionFactory.openSession()) {
            Product result = null;
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String hql = "from Product where id = :id";
                Query<Product> query = session.createQuery(hql, Product.class);
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
    public Iterable<Product> findAll() {
        initialize();
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            List<Product> result = null;
            try {
                tx = session.beginTransaction();
                String hql = "from Product";
                Query<Product> query = session.createQuery(hql, Product.class);
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
