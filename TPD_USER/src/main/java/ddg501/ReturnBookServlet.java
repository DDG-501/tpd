package ddg501;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import ddg501.requests.ReturnBookRequest;

import java.io.IOException;

public class ReturnBookServlet extends HttpServlet {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            InitialContext ctx = new InitialContext();
            UserDAORemote userDao = (UserDAORemote) ctx
                    .lookup("java:global/TPD_EAR/ddg501-TPD_EJB-1.0-SNAPSHOT/UserDAO!ddg501.UserDAO");

            ReturnBookRequest returnBookRequest = objectMapper.readValue(request.getInputStream(),
                    ReturnBookRequest.class);
            User user = userDao.login(returnBookRequest.username, returnBookRequest.password);

            if (user == null) {
                response.setContentType("text/plain");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }

            var bookBorrows = user.getBorrows();
            UserBook bookBorrow = null;

            for (var borrow : bookBorrows) {
                if (borrow.getId() == returnBookRequest.bookborrow_id) {
                    bookBorrow = borrow;
                    break;
                }
            }

            if (bookBorrow == null) {
                response.setContentType("text/plain");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Book borrow not found");
                return;
            }

            try {
                userDao.returnBook(bookBorrow);
                response.setContentType("text/plain");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Book returned successfully");

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
