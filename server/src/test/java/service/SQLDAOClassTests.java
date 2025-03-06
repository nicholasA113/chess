package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SQLDAOClassTests {

    @Test
    @DisplayName("AuthData Table Created Successfully")
    public void authDataTableCreationSuccess() throws DataAccessException {
        AuthData authData = new AuthData( "authToken123", "nicholasUsername");
        SQLAuthDataDAO sqlAuthDataDAO = new SQLAuthDataDAO();
        sqlAuthDataDAO.createAuth(authData);

        try (var conn = DatabaseManager.getConnection()){
            String queryStatement = "SELECT authToken, username FROM authData WHERE authToken = 'authToken123'";
            try (var preparedStatement = conn.prepareStatement(queryStatement)){

            }
        }

    }
}
