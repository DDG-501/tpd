package ddg501.requests;

public class DeleteBookRequest {
    public int book_id;
    public String username;

    public String password;

    public DeleteBookRequest() {
    }

    public DeleteBookRequest(int book_id, String username, String password) {
        this.book_id = book_id;
        this.username = username;
        this.password = password;
    }
}
