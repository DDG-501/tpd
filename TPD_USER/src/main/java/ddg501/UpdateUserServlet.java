package ddg501;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

class UpdateUserRequest {
    public int id;
    public String email;
    public String username;
    public String password;
    public String old_username;
    public String old_password;
}

public class UpdateUserServlet extends HttpServlet {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            InitialContext ctx = new InitialContext();
            UserDAORemote dao = (UserDAORemote) ctx
                    .lookup("java:global/TPD_EAR/ddg501-TPD_EJB-1.0-SNAPSHOT/UserDAO!ddg501.UserDAO");

            UpdateUserRequest updateUserRequest = objectMapper.readValue(request.getInputStream(),
                    UpdateUserRequest.class);

            User userToBeUpdated = dao.get(updateUserRequest.id);

            if (userToBeUpdated == null) {
                response.setContentType("text/plain");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }

            if (!Authorization.isAuthorized(dao, userToBeUpdated.getUsername(), updateUserRequest.old_username,
                    updateUserRequest.old_password)) {
                response.setContentType("text/plain");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }

            User user = new User(updateUserRequest.id, updateUserRequest.username, updateUserRequest.password,
                    updateUserRequest.email);

            try {
                dao.update(user);
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(response.getOutputStream(), user);
            } catch (Exception e) {
                response.setContentType("text/plain");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            }
        } catch (Exception e) {
            response.setContentType("text/plain");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
