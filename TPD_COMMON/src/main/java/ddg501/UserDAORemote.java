package ddg501;

import jakarta.ejb.Remote;
import java.util.List;

@Remote
public interface UserDAORemote {
    List<User> getAll() ;
    User get(String username, String password) ;
    void add(User user);
    void  update(User user);
    void  delete(User user);
}
