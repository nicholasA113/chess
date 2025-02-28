package dataaccess;

import model.AuthData;

public interface AuthDataAccessInterface {
    void createAuth(AuthData auth);
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);
}
