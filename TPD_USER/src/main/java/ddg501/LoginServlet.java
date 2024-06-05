package ddg501;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

class LoginRequest {
    public String user;
    public String password;
}

public class LoginServlet extends HttpServlet {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            InitialContext ctx = new InitialContext();
            UserDAORemote dao = (UserDAORemote) ctx
                    .lookup("java:global/TPD_EAR/ddg501-TPD_EJB-1.0-SNAPSHOT/UserDAO!ddg501.UserDAO");

            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            User user = dao.login(loginRequest.user, loginRequest.password);

            if (user != null) {
                response.setContentType("application/json");
                objectMapper.writeValue(response.getOutputStream(), user);
            } else {
                response.setContentType("text/plain");
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Login failed");
            }
        } catch (Exception e) {
            response.setContentType("text/plain");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
