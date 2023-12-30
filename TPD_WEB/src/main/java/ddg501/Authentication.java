package ddg501;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@RequestScoped
@Named
public class Authentication {
    private String username;
    private String password;

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
        try {
            InitialContext ctx = new InitialContext();

            UserDAORemote dao = (UserDAORemote) ctx.lookup("java:global/TPD_EAR/ddg501-TPD_EJB-1.0-SNAPSHOT/UserDAO!ddg501.UserDAO");

            User user = new User(username, password);

            dao.add(user);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "User added successfully!"));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Couldn't register user: " + e.getMessage()));
        }
    }
}
