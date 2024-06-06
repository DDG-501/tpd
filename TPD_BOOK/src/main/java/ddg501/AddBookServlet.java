package ddg501;

import com.fasterxml.jackson.databind.ObjectMapper;

import ddg501.requests.AddBookRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import java.io.IOException;

public class AddBookServlet extends HttpServlet {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            InitialContext ctx = new InitialContext();
            BookDAORemote dao = (BookDAORemote) ctx
                    .lookup("java:global/TPD_EAR/ddg501-TPD_EJB-1.0-SNAPSHOT/BookDAO!ddg501.BookDAO");

            UserDAORemote daoUser = (UserDAORemote) ctx
                    .lookup("java:global/TPD_EAR/ddg501-TPD_EJB-1.0-SNAPSHOT/UserDAO!ddg501.UserDAO");

            AddBookRequest addBookRequest = objectMapper.readValue(request.getInputStream(), AddBookRequest.class);

            if (daoUser.login(addBookRequest.username, addBookRequest.password) == null) {
                response.setContentType("text/plain");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "You are not logged in!");
                return;
            }

            try {
                dao.add(new Book(addBookRequest.name, addBookRequest.author, addBookRequest.publishDate,
                        addBookRequest.description, addBookRequest.imageURL));
                response.setContentType("text/plain");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Book added successfully");

            } catch (Exception e) {
                response.setContentType("text/plain");
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Failed to add book");
            }
        } catch (Exception e) {
            response.setContentType("text/plain");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
