package ddg501;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(min = 4, max = 50)
    private String username;
    @NotNull
    @Size(min=8, max=50)
    private String password;

    @ManyToMany(mappedBy = "users")
    private Set<Book> books = new HashSet<>();

    public User() {
    }

    public void addBook(Book book) {
        this.books.add(book);
        book.addUser(this);
    }

    public void removeBook(Book book) {
        this.books.remove(book);
        book.removeUser(this);
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
