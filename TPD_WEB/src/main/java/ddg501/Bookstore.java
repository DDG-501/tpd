package ddg501;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import javax.naming.InitialContext;
import java.io.Serializable;
import java.util.List;

@RequestScoped
@Named
public class Bookstore implements Serializable {
    List<Book> books;

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @PostConstruct
    public void init() {
        try {
            InitialContext ctx = new InitialContext();

            BookDAORemote dao = (BookDAORemote) ctx.lookup("java:global/TPD_EAR/ddg501-TPD_EJB-1.0-SNAPSHOT/BookDAO!ddg501.BookDAO");

            books = dao.getAll();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
