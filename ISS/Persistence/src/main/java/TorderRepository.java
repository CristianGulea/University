import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import java.util.List;

public class TorderRepository implements IOrderRepository{
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
    public void add(Torder elem) {
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
    public void delete(Torder elem) {
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
    public void update(Torder elem, Integer id) {
        initialize();
        try(Session session = sessionFactory.openSession()){
            Transaction tx = null;
            try{
                tx = session.beginTransaction();
                Torder torder = session.load(Torder.class, id);
                torder.setId(elem.getId());
                torder.setComments(elem.getComments());
                torder.setPrice(elem.getPrice());
                torder.setCompanyId(elem.getCompanyId());
                torder.setStatus(elem.getStatus());
                torder.setQuantity(elem.getQuantity());
                torder.setProductId(elem.getProductId());
                session.update(torder);
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
    public Torder findById(Integer id) {
        initialize();
        try (Session session = sessionFactory.openSession()) {
            Torder result = null;
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String hql = "from Torder where id = :id";
                Query<Torder> query = session.createQuery(hql, Torder.class);
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
    public Iterable<Torder> findAll() {
        initialize();
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            List<Torder> result = null;
            try {
                tx = session.beginTransaction();
                String hql = "from Torder ";
                Query<Torder> query = session.createQuery(hql, Torder.class);
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
