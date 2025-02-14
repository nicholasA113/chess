package dataaccess;

import model.AuthData;

public interface AuthDataAccessInterface {
    void createAuth(AuthData auth);
    AuthData getAuth(String authToken);
    void updateAuth(AuthData auth);
    void deleteAuth(String authToken);
}
