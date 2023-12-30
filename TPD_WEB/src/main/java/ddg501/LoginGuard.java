package ddg501;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@RequestScoped
@Named
public class LoginGuard {
    @Inject
    private Authentication authentication;

    public void checkAccess() {

        if (authentication == null || authentication.getUser() == null) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();
            navigationHandler.handleNavigation(facesContext, null, "index.xhtml?faces-redirect=true");
        }
    }
}
