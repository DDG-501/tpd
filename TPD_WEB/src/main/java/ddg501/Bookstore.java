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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ddg501.requests.BorrowBookRequest;
import ddg501.requests.GetAllBooksRequest;
import ddg501.requests.ReturnBookRequest;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RequestScoped
@Named
public class Bookstore implements Serializable {
    @Inject
    private Authentication authentication;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient client = Authentication.getHttpClient();

    public List<Book> getBooks() {
        try {
            GetAllBooksRequest getAllBooksRequest = new GetAllBooksRequest(authentication.getUser().getUsername(),
                    authentication.getUser().getPassword());
            String json = objectMapper.writeValueAsString(getAllBooksRequest);

            RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(authentication.getBookEndpoint() + "/TPD_BOOK/get_books")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return objectMapper.readValue(response.body().string(), new TypeReference<List<Book>>() {
                    });
                }
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
        return null;
    }

    public void borrowBook(Book book) {
        try {
            BorrowBookRequest borrowBookRequest = new BorrowBookRequest((int) authentication.getUser().getId(),
                    (int) book.getId(),
                    authentication.getUser().getUsername(), authentication.getUser().getPassword());
            String json = objectMapper.writeValueAsString(borrowBookRequest);

            RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(authentication.getUserEndpoint() + "/TPD_USER/borrow_book")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Book borrowed successfully!"));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                    "Couldn't borrow book: " + response.body().string()));
                }
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Couldn't borrow book: " + e.getMessage()));
        }
    }

    public void returnBook(UserBook userBook) {
        try {
            ReturnBookRequest borrowBookRequest = new ReturnBookRequest((int) userBook.getId(),
                    authentication.getUser().getUsername(), authentication.getUser().getPassword());
            String json = objectMapper.writeValueAsString(borrowBookRequest);

            RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(authentication.getUserEndpoint() + "/TPD_USER/return_book")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Book returned successfully!"));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                    "Couldn't return book: " + response.body().string()));
                }
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Couldn't return book: " + e.getMessage()));
        }
    }

    public String formatDate(Date date) {
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = dateFormat.format(date);
            return formattedDate;
        }

        return "";
    }

    public String limitDescription(String description) {
        int maxLength = 100;
        if (description != null && description.length() > maxLength) {
            return description.substring(0, maxLength) + "...";
        } else {
            return description;
        }
    }
}
