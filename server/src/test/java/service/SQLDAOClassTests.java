package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SQLDAOClassTests {

    @Test
    @DisplayName("AuthData Table Created Successfully")
    public void authDataTableCreationSuccess() throws DataAccessException {
        AuthData authData = new AuthData( "authToken123", "nicholasUsername");
        SQLAuthDataDAO sqlAuthDataDAO = new SQLAuthDataDAO();

        sqlAuthDataDAO.createAuth(authData);

        try (var conn = DatabaseManager.getConnection()){
            String queryStatement = "SELECT authToken, username FROM authData WHERE authToken = ?";
            var preparedStatement = conn.prepareStatement(queryStatement);
            preparedStatement.setString(1, "authToken123");
            try (var result = preparedStatement.executeQuery()){
                String authTokenResult = result.getString("authToken");
                String usernameResult = result.getString("username");

                Assertions.assertEquals("authToken123", authTokenResult,
                        "AuthTokens do not match");
                Assertions.assertEquals("nicholasUsername", usernameResult,
                        "Usernames do not match");
            }
        }
        catch (SQLException e){
            throw new DataAccessException("Error creating auth data: " + e.getMessage());
        }
    }
}
