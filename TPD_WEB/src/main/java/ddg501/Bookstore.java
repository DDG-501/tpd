package ddg501;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import javax.naming.InitialContext;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RequestScoped
@Named
public class Bookstore implements Serializable {
    @Inject
    private Authentication authentication;

    @PostConstruct
    public void init() {

    }

    public List<Book> getBooks() {
        try {
            InitialContext ctx = new InitialContext();

            BookDAORemote dao = (BookDAORemote) ctx
                    .lookup("java:global/TPD_EAR/ddg501-TPD_EJB-1.0-SNAPSHOT/BookDAO!ddg501.BookDAO");
            return dao.getAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void borrowBook(Book book) {
        try {
            InitialContext ctx = new InitialContext();

            UserDAORemote dao = (UserDAORemote) ctx
                    .lookup("java:global/TPD_EAR/ddg501-TPD_EJB-1.0-SNAPSHOT/UserDAO!ddg501.UserDAO");

            dao.borrowBook(authentication.getUser(), book);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Book borrowed successfully!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Couldn't borrow book: " + e.getMessage()));
        }
    }

    public void returnBook(UserBook userBook) {
        try {
            InitialContext ctx = new InitialContext();

            UserDAORemote dao = (UserDAORemote) ctx
                    .lookup("java:global/TPD_EAR/ddg501-TPD_EJB-1.0-SNAPSHOT/UserDAO!ddg501.UserDAO");

            dao.returnBook(userBook);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Book returned successfully!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Couldn't return book: " + e.getMessage()));
        }
    }

    public String formatDate(Date date) {
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = dateFormat.format(date);
            return formattedDate;
        }

        return "";
    }

    public String limitDescription(String description) {
        int maxLength = 100;
        if (description != null && description.length() > maxLength) {
            return description.substring(0, maxLength) + "...";
        } else {
            return description;
        }
    }
}
