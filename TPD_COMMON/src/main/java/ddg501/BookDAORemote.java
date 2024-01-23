package ddg501;

import java.util.List;

import jakarta.ejb.Remote;

@Remote
public interface BookDAORemote {

    List<Book> getAll();

    Book get(long id);

    void add(Book book);

    void update(Book book);

    void delete(Book book);

}
