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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

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
    private final String userEndpoint = getFromConfig("user_endpoint");
    private final String bookEndpoint = getFromConfig("book_endpoint");

    public String getBookEndpoint() {
        return bookEndpoint;
    }

    public String getUserEndpoint() {
        return userEndpoint;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient client = getHttpClient();

    public static OkHttpClient getHttpClient() {
        try {

            FileInputStream fis = new FileInputStream(getFromConfig("certificate_path"));
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate caCert = (X509Certificate) cf.generateCertificate(fis);

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("caCert", caCert);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            TrustManager[] trustManagers = tmf.getTrustManagers();

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());

            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0])
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .build();

            return client;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String getFromConfig(String setting) {
        Properties properties = new Properties();
        try (InputStream input = Authentication.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                return "";
            }

            properties.load(input);
            String endpoint = properties.getProperty(setting);
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
                    .url(userEndpoint + "/TPD_USER/login")
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
                    .url(userEndpoint + "/TPD_USER/register")
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
                    .url(userEndpoint + "/TPD_USER/login")
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
