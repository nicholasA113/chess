package dataaccess;

import model.AuthData;

import java.util.Map;

public class SQLAuthDataDAO implements AuthDataAccessInterface{


    public void createAuth(AuthData auth) {

    }

    public AuthData getAuth(String authToken) {
        return null;
    }

    public void deleteAuth(String authToken) {

    }

    public Map<String, AuthData> getAllAuthData() {
        return Map.of();
    }

    public void clearAllAuthData() {

    }
}
