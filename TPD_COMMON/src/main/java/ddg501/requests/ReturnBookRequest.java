package ddg501.requests;

public class ReturnBookRequest {
    public int bookborrow_id;
    public String username;
    public String password;

    public ReturnBookRequest() {
    }

    public ReturnBookRequest(int bookborrow_id, String username, String password) {
        this.bookborrow_id = bookborrow_id;
        this.username = username;
        this.password = password;
    }
}
