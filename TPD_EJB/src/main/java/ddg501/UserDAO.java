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
}
