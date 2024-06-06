package ddg501;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.fasterxml.jackson.databind.ObjectMapper;

import ddg501.requests.AddUserRequest;
import ddg501.requests.LoginRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

@SessionScoped
@Named
public class Authentication implements Serializable {
    private String username;
    private String password;
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private User user;
    private final String userEndpoint = getUserEndpointFromConfig();
    private final String bookEndpoint = getBookEndpointFromConfig();

    public String getBookEndpoint() {
        return bookEndpoint;
    }

    public String getUserEndpoint() {
        return userEndpoint;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient client = new OkHttpClient();

    private String getUserEndpointFromConfig() {
        Properties properties = new Properties();
        try (InputStream input = Authentication.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                return "";
            }

            properties.load(input);
            String endpoint = properties.getProperty("user_endpoint");
            return endpoint;

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return "";
    }

    private String getBookEndpointFromConfig() {
        Properties properties = new Properties();
        try (InputStream input = Authentication.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                return "";
            }

            properties.load(input);
            String endpoint = properties.getProperty("book_endpoint");
            return endpoint;

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return "";
    }

    public User getUser() {
        return user;
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

    public void refreshUser() {
        try {
            LoginRequest loginRequest = new LoginRequest(user.getUsername(), user.getPassword());
            String json = objectMapper.writeValueAsString(loginRequest);

            RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url("http://" + userEndpoint + "/TPD_USER/login")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    user = objectMapper.readValue(response.body().string(), User.class);
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Can't refresh user"));
                }
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Couldn't refresh user: " + e.getMessage()));
        }
    }

    public void register() {
        try {
            AddUserRequest registerRequest = new AddUserRequest(getUsername(), getPassword(), getEmail());
            String json = objectMapper.writeValueAsString(registerRequest);

            RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url("http://" + userEndpoint + "/TPD_USER/register")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "User added successfully!"));
                    FacesContext facesContext = FacesContext.getCurrentInstance();
                    NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();
                    navigationHandler.handleNavigation(facesContext, null, "index.xhtml?faces-redirect=true");

                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                    "Couldn't register user: " + response.body().string()));
                }
            }

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Couldn't register user: " + e.getMessage()));
        }
    }

    public void login() {
        try {
            LoginRequest loginRequest = new LoginRequest(getUsername(), getPassword());
            String json = objectMapper.writeValueAsString(loginRequest);

            RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url("http://" + userEndpoint + "/TPD_USER/login")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    username = "";
                    password = "";
                    email = "";

                    user = objectMapper.readValue(response.body().string(), User.class);

                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "User logged in!"));
                    FacesContext facesContext = FacesContext.getCurrentInstance();
                    NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();
                    navigationHandler.handleNavigation(facesContext, null, "bookstore.xhtml?faces-redirect=true");
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Incorrect credentials"));
                }

            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Couldn't register user: " + e.getMessage()));
        }
    }

    public void moveToRegister() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();
        navigationHandler.handleNavigation(facesContext, null, "register.xhtml?faces-redirect=true");
    }

    public void logout() {
        user = null;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();
        navigationHandler.handleNavigation(facesContext, null, "index.xhtml?faces-redirect=true");
    }
}
