package ddg501;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.List;
import java.util.Set;


@Stateless
@LocalBean
public class BookDAO implements BookDAORemote {

    @PersistenceContext(unitName = "Postgres")
    private EntityManager entityManager;

    public BookDAO() {

    }
    @Override
    public List<Book> getAll() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> criteriaQuery = criteriaBuilder.createQuery(Book.class);

        Root<Book> root = criteriaQuery.from(Book.class);
        criteriaQuery.select(root);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public Book get(long id) {
        return entityManager.find(Book.class, id);
    }

    @Override
    public void add(Book book) throws IllegalArgumentException{
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<Book>> violations = validator.validate(book);

            if (!violations.isEmpty()) {
                throw new IllegalArgumentException(violations.toString());
            }

            entityManager.persist(book);
        }
    }

    @Override
    public void update(Book book) throws IllegalArgumentException{
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<Book>> violations = validator.validate(book);

            if (!violations.isEmpty()) {
                throw new IllegalArgumentException(violations.toString());
            }

            entityManager.merge(book);
        }
    }

    @Override
    public void delete(Book book) {
        entityManager.remove(book);
    }
}
