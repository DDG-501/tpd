package ddg501;

import com.fasterxml.jackson.databind.ObjectMapper;

import ddg501.requests.DeleteBookRequest;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import java.io.IOException;

public class DeleteBookServlet extends HttpServlet {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            InitialContext ctx = new InitialContext();
            BookDAORemote dao = (BookDAORemote) ctx
                    .lookup("java:global/TPD_EAR_BOOK/ddg501-TPD_EJB-1.0-SNAPSHOT/BookDAO!ddg501.BookDAO");

            UserDAORemote daoUser = (UserDAORemote) ctx
                    .lookup("java:global/TPD_EAR_BOOK/ddg501-TPD_EJB-1.0-SNAPSHOT/UserDAO!ddg501.UserDAO");

            DeleteBookRequest deleteBookRequest = objectMapper.readValue(request.getInputStream(),
                    DeleteBookRequest.class);

            if (daoUser.login(deleteBookRequest.username, deleteBookRequest.password) == null) {
                response.setContentType("text/plain");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "You are not logged in!");
                return;
            }

            try {
                var book = dao.get(deleteBookRequest.book_id);

                if (book == null) {
                    response.setContentType("text/plain");
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
                    return;
                }

                dao.delete(book);
                response.setContentType("text/plain");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Book deleted successfully");

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
