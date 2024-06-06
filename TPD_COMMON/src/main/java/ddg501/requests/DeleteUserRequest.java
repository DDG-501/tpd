package ddg501.requests;

public class DeleteUserRequest {
    public int id;
    public String username;
    public String password;

    public DeleteUserRequest() {
    }

    public DeleteUserRequest(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }
}
