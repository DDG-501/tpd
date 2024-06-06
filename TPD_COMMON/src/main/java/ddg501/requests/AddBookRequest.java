package ddg501.requests;

import java.util.Date;

public class AddBookRequest {
    public String username;

    public String password;

    public String name;

    public String author;

    public Date publishDate;

    public String description;

    public String imageURL;

    public AddBookRequest() {
    }

    public AddBookRequest(String username, String password, String name, String author, Date publishDate,
            String description, String imageURL) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.author = author;
        this.publishDate = publishDate;
        this.description = description;
        this.imageURL = imageURL;
    }

}
