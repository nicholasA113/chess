package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLUserDataDAO implements UserDataAccess {

    public SQLUserDataDAO() throws DataAccessException{
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.configureDatabase();
    }

    public void insertUser(UserData user) throws DataAccessException{
        String insertStatement = "INSERT INTO userData (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement(insertStatement)){
                preparedStatement.setString(1, user.username());
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, user.email());
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e){
            throw new DataAccessException("Error inserting user into database: " + e.getMessage());
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        String getStatement = "SELECT username, password, email FROM userData WHERE username = ?";
        try (var conn = DatabaseManager.getConnection()){
            var preparedStatement = conn.prepareStatement(getStatement);
            preparedStatement.setString(1, username);
            try (var result = preparedStatement.executeQuery()){
                if (result.next()){
                    return new UserData(result.getString("username"),
                            result.getString("password"),
                            result.getString("email"));
                }
                else{
                    throw new DataAccessException("Requested user is not in the database");
                }
            }
        }
        catch (SQLException e){
            throw new DataAccessException("Error getting user from database : " + e.getMessage());
        }
    }

    public ArrayList<UserData> getAllUserData() throws DataAccessException {
        String getStatement = "SELECT * FROM userData";
        ArrayList<UserData> userDataList = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()){
            var preparedStatement = conn.prepareStatement(getStatement);
            var result = preparedStatement.executeQuery();
            while (result.next()){
                String username = result.getString("username");
                String password = result.getString("password");
                String email = result.getString("email");
                userDataList.add(new UserData(username, password, email));
            }
        }
        catch (SQLException e){
            throw new DataAccessException("Error getting all the userData: " + e.getMessage());
        }
        return userDataList;
    }

    public void clearAllUserData() throws DataAccessException {
        String clearStatement = "DELETE FROM userData";
        try (var conn = DatabaseManager.getConnection()){
            var preparedStatement = conn.prepareStatement(clearStatement);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            throw new DataAccessException("Error clearing the database: " + e.getMessage());
        }
    }
}
