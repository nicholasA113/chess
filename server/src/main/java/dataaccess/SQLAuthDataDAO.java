package dataaccess;

import model.AuthData;
import com.google.gson.Gson;

import java.sql.*;

import java.util.Map;

public class SQLAuthDataDAO implements AuthDataAccessInterface{

    private final String[] createAuthDataStatements = {
            """
            CREATE TABLE IF NOT EXISTS authData (
            'authToken' VARCHAR(256) PRIMARY KEY,
            'username' VARCHAR(256) NOT NULL
            );
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()){
            for (String statement : createAuthDataStatements){
                try (var preparedStatement = conn.prepareStatement(statement)){
                    preparedStatement.executeUpdate();
                }
            }
        }
        catch (SQLException e){
            throw new DataAccessException(
                    String.format("Unable to configure database: %s", e.getMessage()));
        }
    }

    public void createAuth(AuthData auth) throws DataAccessException {
        String statement = "INSERT INTO authData (authToken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, auth.authToken());
                preparedStatement.setString(2, auth.username());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to insert auth data: %s", e.getMessage()));
        }
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
