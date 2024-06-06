package ddg501.requests;

public class BorrowBookRequest {
    public int user_id;
    public int book_id;
    public String username;
    public String password;

    public BorrowBookRequest() {
    }

    public BorrowBookRequest(int user_id, int book_id, String username, String password) {
        this.user_id = user_id;
        this.book_id = book_id;
        this.username = username;
        this.password = password;
    }
}
