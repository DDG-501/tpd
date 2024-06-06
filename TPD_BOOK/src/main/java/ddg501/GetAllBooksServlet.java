package ddg501;

import com.fasterxml.jackson.databind.ObjectMapper;

import ddg501.requests.GetAllBooksRequest;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import java.io.IOException;

public class GetAllBooksServlet extends HttpServlet {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            InitialContext ctx = new InitialContext();
            BookDAORemote dao = (BookDAORemote) ctx
                    .lookup("java:global/TPD_EAR_BOOK/ddg501-TPD_EJB-1.0-SNAPSHOT/BookDAO!ddg501.BookDAO");

            UserDAORemote daoUser = (UserDAORemote) ctx
                    .lookup("java:global/TPD_EAR_BOOK/ddg501-TPD_EJB-1.0-SNAPSHOT/UserDAO!ddg501.UserDAO");

            GetAllBooksRequest getBooksRequest = objectMapper.readValue(request.getInputStream(),
                    GetAllBooksRequest.class);

            if (daoUser.login(getBooksRequest.username, getBooksRequest.password) == null) {
                response.setContentType("text/plain");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "You are not logged in!");
                return;
            }

            try {
                var books = dao.getAll();
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(response.getOutputStream(), books);
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
