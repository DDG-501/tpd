package ddg501.requests;

public class GetBookRequest {
    public int book_id;
    public String username;
    public String password;

    public GetBookRequest() {
    }

    public GetBookRequest(int book_id, String username, String password) {
        this.book_id = book_id;
        this.username = username;
        this.password = password;
    }
}
