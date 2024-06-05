package ddg501;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import java.io.IOException;

class GetBookRequest {
    public int book_id;
    public String username;
    public String password;
}

public class GetBookServlet extends HttpServlet {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            InitialContext ctx = new InitialContext();
            BookDAORemote dao = (BookDAORemote) ctx
                    .lookup("java:global/TPD_EAR/ddg501-TPD_EJB-1.0-SNAPSHOT/BookDAO!ddg501.BookDAO");

            UserDAORemote daoUser = (UserDAORemote) ctx
                    .lookup("java:global/TPD_EAR/ddg501-TPD_EJB-1.0-SNAPSHOT/UserDAO!ddg501.UserDAO");

            GetBookRequest getBookRequest = objectMapper.readValue(request.getInputStream(),
                    GetBookRequest.class);

            if (daoUser.login(getBookRequest.username, getBookRequest.password) == null) {
                response.setContentType("text/plain");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "You are not logged in!");
                return;
            }

            try {
                var book = dao.get(getBookRequest.book_id);

                if (book == null) {
                    response.setContentType("text/plain");
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found!");
                    return;
                }

                response.setContentType("application/json");
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
