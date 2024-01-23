package ddg501;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
        User user = entityManager.find(User.class, id);

        entityManager.refresh(user);

        return user;
    }

    public void add(User user) throws IllegalArgumentException {
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

    public void update(User user) throws IllegalArgumentException {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            User existingUser = get(user.getId());
            if (existingUser == null) {
                throw new IllegalArgumentException("User does not exist in the database");
            }

            if (!violations.isEmpty()) {
                String violationMessages = violations.stream()
                        .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                        .collect(Collectors.joining(", "));

                throw new IllegalArgumentException(violationMessages);
            }

            existingUser.setPassword(user.getPassword());
            existingUser.setUsername(user.getUsername());

            entityManager.merge(existingUser);
        }
    }

    public void delete(User user) {
        User existingUser = get(user.getId());
        if (existingUser == null) {
            throw new IllegalArgumentException("User does not exist in the database");
        }

        entityManager.remove(existingUser);
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
    public void returnBook(UserBook borrow) {
        borrow.setReturnDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        entityManager.merge(borrow);
    }
}
