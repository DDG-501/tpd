package ddg501;

public interface UserBookRemote {
    void borrowBook(User user, Book book);
    void returnBook(User user, Book book);

}
