package ddg501;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

class DeleteUserRequest {
    public int id;
    public String username;
    public String password;
}

public class DeleteUserServlet extends HttpServlet {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            InitialContext ctx = new InitialContext();
            UserDAORemote dao = (UserDAORemote) ctx
                    .lookup("java:global/TPD_EAR/ddg501-TPD_EJB-1.0-SNAPSHOT/UserDAO!ddg501.UserDAO");

            DeleteUserRequest deleteUserRequest = objectMapper.readValue(request.getInputStream(),
                    DeleteUserRequest.class);
            User userToBeDeleted = dao.get(deleteUserRequest.id);

            if (userToBeDeleted == null) {
                response.setContentType("text/plain");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }

            if (!Authorization.isAuthorized(dao, userToBeDeleted.getUsername(), deleteUserRequest.username,
                    deleteUserRequest.password)) {
                response.setContentType("text/plain");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }

            try {
                dao.delete(dao.get(deleteUserRequest.id));
                response.setContentType("text/plain");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("User deleted successfully");

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
