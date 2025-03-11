package dataaccess;

import model.AuthData;

import java.sql.*;

import java.util.HashMap;
import java.util.Map;

public class SQLAuthDataDAO implements AuthDataAccess {

    public SQLAuthDataDAO() throws DataAccessException{
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.configureDatabase();
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
            throw new DataAccessException("Unable to insert auth data: " + e.getMessage());
        }
    }

    public AuthData getAuth(String authToken) throws DataAccessException{
        String queryStatement = "SELECT authToken, username FROM authData WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection()){
            var preparedStatement = conn.prepareStatement(queryStatement);
            preparedStatement.setString(1, authToken);
            try (var result = preparedStatement.executeQuery()){
                if (result.next()){
                    return new AuthData(result.getString("authToken"),
                            result.getString("username"));
                }
                else{
                    throw new DataAccessException("AuthToken is not found in the database");
                }
            }
        }
        catch (SQLException e){
            throw new DataAccessException("Unable to get auth data: " + e.getMessage());
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException{
        String deleteStatement = "DELETE FROM authData WHERE authToken = ?";
        try(var conn = DatabaseManager.getConnection()){
            var preparedStatement = conn.prepareStatement(deleteStatement);
            preparedStatement.setString(1, authToken);
            var result = preparedStatement.executeUpdate();
            if (result == 0){
                throw new DataAccessException("AuthToken not found in database to delete");
            }
        }
        catch (SQLException e){
            throw new DataAccessException("Error deleting auth data: " + e.getMessage());
        }
    }

    public Map<String, AuthData> getAllAuthData() throws DataAccessException{
        String getAllStatement = "SELECT * FROM authData";
        Map<String, AuthData> authDataList = new HashMap<>();

        try (var conn = DatabaseManager.getConnection()){
            var preparedStatement = conn.prepareStatement(getAllStatement);
            var result = preparedStatement.executeQuery();
            while (result.next()){
                String authToken = result.getString("authToken");
                String username = result.getString("username");
                authDataList.put(authToken, new AuthData(authToken, username));
            }
        }
        catch (SQLException e){
            throw new DataAccessException("Error getting all of the data: " + e.getMessage());
        }
        return authDataList;
    }

    public void clearAllAuthData() throws DataAccessException {
        String deleteAllStatement = "DELETE FROM authData";
        try (var conn = DatabaseManager.getConnection()){
            var preparedStatement = conn.prepareStatement(deleteAllStatement);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            throw new DataAccessException("Error deleting rows: " + e.getMessage());
        }
    }
}
