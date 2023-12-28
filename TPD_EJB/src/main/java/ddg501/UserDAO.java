package ddg501;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.List;
import java.util.Set;

@Stateless
@LocalBean
public class UserDAO implements UserDAORemote {
    @PersistenceContext(unitName = "Postgres")
    private EntityManager entityManager;


    public UserDAO() {
    }

    public List<User> getAll() {
        String jpql = "SELECT e FROM User e";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        return query.getResultList();
    }

    public User get(String username, String password) {
        String jpql = "SELECT e FROM User e WHERE e.username = :username AND e.password = :password";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("username", username);
        query.setParameter("password", password);

        List<User> resultList = query.getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public void add(User user) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        System.out.println(violations);

        if (!violations.isEmpty()) {
            throw new IllegalArgumentException("Validation failed for user: " + violations);
        }

        try {
            entityManager.persist(user);
            entityManager.flush();
        } catch (Exception e) {
            throw new RuntimeException("Error while adding user", e);
        }
    }

    public void update(User user) {
        entityManager.merge(user);
    }

    public void delete(User user) {
        entityManager.remove(user);
    }
}
