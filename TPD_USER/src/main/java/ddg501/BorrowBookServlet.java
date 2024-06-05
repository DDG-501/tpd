package ddg501;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

class BorrowBookRequest {
    public int user_id;
    public int book_id;
    public String username;
    public String password;
}

public class BorrowBookServlet extends HttpServlet {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            InitialContext ctx = new InitialContext();
            UserDAORemote userDao = (UserDAORemote) ctx
                    .lookup("java:global/TPD_EAR/ddg501-TPD_EJB-1.0-SNAPSHOT/UserDAO!ddg501.UserDAO");

            BookDAORemote bookDao = (BookDAORemote) ctx
                    .lookup("java:global/TPD_EAR/ddg501-TPD_EJB-1.0-SNAPSHOT/BookDAO!ddg501.BookDAO");

            BorrowBookRequest borrowBookRequest = objectMapper.readValue(request.getInputStream(),
                    BorrowBookRequest.class);
            User user = userDao.get(borrowBookRequest.user_id);

            if (user == null) {
                response.setContentType("text/plain");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }

            Book book = bookDao.get(borrowBookRequest.book_id);

            if (book == null) {
                response.setContentType("text/plain");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
                return;
            }

            if (!Authorization.isAuthorized(userDao, user.getUsername(), borrowBookRequest.username,
                    borrowBookRequest.password)) {
                response.setContentType("text/plain");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }

            try {
                userDao.borrowBook(user, book);
                response.setContentType("text/plain");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Book borrowed successfully");

            } catch (Exception e) {
                response.setContentType("text/plain");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            }
        } catch (Exception e) {
            response.setContentType("text/plain");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
