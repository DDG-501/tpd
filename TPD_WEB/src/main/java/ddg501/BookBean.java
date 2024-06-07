package ddg501;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.Serializable;

import com.fasterxml.jackson.databind.ObjectMapper;

import ddg501.requests.AddBookRequest;

@Named
@RequestScoped
public class BookBean implements Serializable {
    @Inject
    private Authentication authentication;

    private Book book;

    public BookBean() {
        book = new Book();
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient client = Authentication.getHttpClient();

    public void addBook() {
        try {

            AddBookRequest addBookRequest = new AddBookRequest(authentication.getUser().getUsername(),
                    authentication.getUser().getPassword(), book.getName(), book.getAuthor(), book.getPublishDate(),
                    book.getDescription(), book.getImageURL());
            String json = objectMapper.writeValueAsString(addBookRequest);

            RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(authentication.getBookEndpoint() + "/TPD_BOOK/add_book")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Book added successfully!"));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                    "Couldn't register user: " + response.body().string()));
                }
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Couldn't add book: " + e.getMessage()));
        }

    }
}
