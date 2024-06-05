package ddg501;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import java.io.IOException;
import java.util.Date;

class UpdateBookRequest {
    public int book_id;

    public String username;

    public String password;

    public String name;

    public String author;

    public Date publishDate;

    public String description;

    public String imageURL;


}
public class UpdateBookServlet extends HttpServlet {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            InitialContext ctx = new InitialContext();
            BookDAORemote dao = (BookDAORemote) ctx
                    .lookup("java:global/TPD_EAR/ddg501-TPD_EJB-1.0-SNAPSHOT/BookDAO!ddg501.BookDAO");

            UserDAORemote daoUser = (UserDAORemote) ctx
                    .lookup("java:global/TPD_EAR/ddg501-TPD_EJB-1.0-SNAPSHOT/UserDAO!ddg501.UserDAO");

            UpdateBookRequest updateBookRequest = objectMapper.readValue(request.getInputStream(),
                    UpdateBookRequest.class);


            if(daoUser.login(updateBookRequest.username, updateBookRequest.password) == null) {
                response.setContentType("text/plain");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "You are not logged in!");
                return;
            }

            Book book = new Book(updateBookRequest.book_id, updateBookRequest.name, updateBookRequest.author, updateBookRequest.publishDate,
                    updateBookRequest.description, updateBookRequest.imageURL);


            try {
                dao.update(book);
                response.setContentType("text/plain");
                response.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(response.getOutputStream(), book);

            } catch (Exception e) {
                response.setContentType("text/plain");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
            }

        } catch (Exception e) {
            response.setContentType("text/plain");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
