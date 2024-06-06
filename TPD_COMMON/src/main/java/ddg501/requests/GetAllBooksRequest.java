package ddg501.requests;

public class GetAllBooksRequest {
    public String username;
    public String password;

    public GetAllBooksRequest() {
    }

    public GetAllBooksRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
