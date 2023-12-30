package ddg501;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
@LocalBean
public class UserDAO implements UserDAORemote {
    @PersistenceContext(unitName = "Postgres")
    private EntityManager entityManager;

    public UserDAO() {
    }

    public List<User> getAll() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(root);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public User get(long id) {
        return entityManager.find(User.class, id);
    }

    public void add(User user) throws IllegalArgumentException{
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            if (!violations.isEmpty()) {
                String violationMessages = violations.stream()
                        .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                        .collect(Collectors.joining(", "));

                throw new IllegalArgumentException(violationMessages);
            }

            entityManager.persist(user);
        }
    }

    public void update(User user) throws IllegalArgumentException{
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            if (!violations.isEmpty()) {
                throw new IllegalArgumentException(violations.toString());
            }

            entityManager.merge(user);
        }
    }

    public void delete(User user) {
        entityManager.remove(user);
    }

    @Override
    public User login(String username, String password) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(root);

        Predicate usernamePredicate = criteriaBuilder.equal(root.get("username"), username);
        Predicate passwordPredicate = criteriaBuilder.equal(root.get("password"), password);
        criteriaQuery.where(criteriaBuilder.and(usernamePredicate, passwordPredicate));

        List<User> resultList = entityManager.createQuery(criteriaQuery).getResultList();

        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public void borrowBook(User user, Book book) {
        UserBook userBook = new UserBook();
        userBook.setUser(user);
        userBook.setBook(book);
        userBook.setBorrowDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        entityManager.persist(userBook);
    }

    @Override
    public void returnBook(User user, Book book) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserBook> criteriaQuery = criteriaBuilder.createQuery(UserBook.class);

        Root<UserBook> root = criteriaQuery.from(UserBook.class);
        criteriaQuery.select(root);

        Predicate userIdPredicate = criteriaBuilder.equal(root.get("user_id"), user.getId());
        Predicate bookIdPredicate = criteriaBuilder.equal(root.get("book_id"), book.getId());
        criteriaQuery.where(criteriaBuilder.and(userIdPredicate, bookIdPredicate));

        List<UserBook> resultList = entityManager.createQuery(criteriaQuery).getResultList();
        resultList.stream().filter(v -> v.getReturnDate() == null).forEach(v -> v.setReturnDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())));
    }
}
