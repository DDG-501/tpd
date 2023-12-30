package ddg501;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

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
    public void add(Book book) throws IllegalArgumentException {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<Book>> violations = validator.validate(book);

            if (!violations.isEmpty()) {
                String violationMessages = violations.stream()
                        .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                        .collect(Collectors.joining(", "));

                throw new IllegalArgumentException(violationMessages);
            }

            entityManager.persist(book);
        }
    }

    @Override
    public void update(Book book) throws IllegalArgumentException {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<Book>> violations = validator.validate(book);

            if (!violations.isEmpty()) {
                String violationMessages = violations.stream()
                        .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                        .collect(Collectors.joining(", "));

                throw new IllegalArgumentException(violationMessages);
            }

            entityManager.merge(book);
        }
    }

    @Override
    public void delete(Book book) {
        entityManager.remove(book);
    }
}
