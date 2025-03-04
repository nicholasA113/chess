package dataaccess;

import model.AuthData;

import java.util.Map;

public interface AuthDataAccessInterface {
    void createAuth(AuthData auth);
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);
    Map<String, AuthData> getAllAuthData();
    void clearAllAuthData();
}
