package ddg501;

import java.util.List;

public interface BookDAORemote {

    List<Book> getAll();
    Book get(long id);
    void add(Book book);
    void update(Book book);
    void delete(Book book);

}
