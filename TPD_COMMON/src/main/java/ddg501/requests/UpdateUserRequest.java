package ddg501.requests;

public class UpdateUserRequest {
    public int id;
    public String email;
    public String username;
    public String password;
    public String old_username;
    public String old_password;

    public UpdateUserRequest() {
    }

    public UpdateUserRequest(int id, String email, String username, String password, String old_username,
            String old_password) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.old_username = old_username;
        this.old_password = old_password;
    }
}
