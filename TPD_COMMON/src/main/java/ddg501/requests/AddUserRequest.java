package ddg501.requests;

public class AddUserRequest {
    public String username;
    public String password;
    public String email;

    public AddUserRequest() {
    }

    public AddUserRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
