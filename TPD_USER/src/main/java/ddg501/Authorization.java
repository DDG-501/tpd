package ddg501;

public class Authorization {

    static boolean isAuthorized(UserDAORemote dao, String usernameToBeModified, String username, String password) {
        if (dao.login(username, password) == null || !username.equals(usernameToBeModified)) {
            return false;
        } else {
            return true;
        }
    }
}
