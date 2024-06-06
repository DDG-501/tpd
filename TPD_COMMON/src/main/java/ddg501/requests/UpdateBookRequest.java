package ddg501.requests;

import java.util.Date;

public class UpdateBookRequest {
    public int book_id;

    public String username;

    public String password;

    public String name;

    public String author;

    public Date publishDate;

    public String description;

    public String imageURL;

    public UpdateBookRequest() {
    }

    public UpdateBookRequest(String username, String password, String name, String author, Date publishDate,
            String description, String imageURL, int book_id) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.author = author;
        this.publishDate = publishDate;
        this.description = description;
        this.imageURL = imageURL;
        this.book_id = book_id;
    }

}
