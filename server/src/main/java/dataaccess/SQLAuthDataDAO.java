package dataaccess;

import model.AuthData;
import com.google.gson.Gson;

import java.sql.*;

import java.util.Map;

public class SQLAuthDataDAO implements AuthDataAccessInterface{


    public void createAuth(AuthData auth) {
        var statement = "INSERT INTO authData (authToken, username) values (?, ?)";
        var json = new Gson().toJson(auth);

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
