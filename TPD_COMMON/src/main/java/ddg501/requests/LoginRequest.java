package ddg501.requests;

public class LoginRequest {
    public String user;
    public String password;

    public LoginRequest() {
    }

    public LoginRequest(String user, String password) {
        this.user = user;
        this.password = password;
    }
}
