package ddg501;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import javax.naming.InitialContext;
import javax.naming.NamingException;

@RequestScoped
@Named
public class Authentication {
    private String username;
    private String password;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void register() {
        status="Failed";
        try {
            InitialContext ctx = new InitialContext();

            UserDAORemote dao = (UserDAORemote) ctx.lookup("java:global/TPD_EAR/ddg501-TPD_EJB-1.0-SNAPSHOT/UserDAO!ddg501.UserDAO");

            User user = new User(username, password);

            dao.add(user);
            status="Success";
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
}
