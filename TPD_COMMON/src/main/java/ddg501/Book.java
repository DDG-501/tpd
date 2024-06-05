package ddg501;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "books", schema = "public")
public class Book implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(min = 4, max = 50)
    @Column(length = 50)
    private String name;

    @NotNull
    @Column(length = 50)
    @Size(max = 50)
    private String author;
    @NotNull
    private Date publishDate;

    @Column(length = 2000)
    @Size(min = 4, max = 2000)
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String imageURL;

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @OneToMany(mappedBy = "book")
    private List<UserBook> borrows = new ArrayList<>();

    public List<UserBook> getBorrows() {
        return borrows;
    }

    public Book() {

    }

    public Book(String name, String author, Date publishDate, String description, String imageURL) {
        this.name = name;
        this.author = author;
        this.publishDate = publishDate;
        this.description = description;
        this.imageURL = imageURL;
    }

    public Book(String name, String author, Date publishDate) {
        this.name = name;
        this.author = author;
        this.publishDate = publishDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }
}
