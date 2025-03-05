package dataaccess;

import model.AuthData;

import java.util.Map;

public interface AuthDataAccessInterface {
    void createAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);

    Map<String, AuthData> getAllAuthData();
    void clearAllAuthData();
}
