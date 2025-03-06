package dataaccess;

import model.AuthData;

import java.util.Map;

public interface AuthDataAccessInterface {
    void createAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;

    Map<String, AuthData> getAllAuthData() throws DataAccessException;
    void clearAllAuthData() throws DataAccessException;
}
