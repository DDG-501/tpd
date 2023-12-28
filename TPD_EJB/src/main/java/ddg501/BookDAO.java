package ddg501;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class BookDAO implements BookDAORemote {

    @PersistenceContext(unitName = "Postgres")
    private EntityManager entityManager;

    public BookDAO() {

    }
    @Override
    public List<Book> getAll() {
        String jpql = "SELECT e FROM Book e";
        TypedQuery<Book> query = entityManager.createQuery(jpql, Book.class);
        return query.getResultList();
    }

    @Override
    public Book get(long id) {
        return entityManager.find(Book.class, id);
    }

    @Override
    public void add(Book book) {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            entityManager.persist(book);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void update(Book book) {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            entityManager.merge(book);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Book book) {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            entityManager.remove(entityManager.contains(book) ? book : entityManager.merge(book));

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
