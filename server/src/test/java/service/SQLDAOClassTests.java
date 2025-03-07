package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SQLDAOClassTests {

    private SQLAuthDataDAO authDataSQL;

    @BeforeEach
    public void setup() throws DataAccessException {
        try{
            authDataSQL = new SQLAuthDataDAO();
            authDataSQL.clearAllAuthData();
        }
        catch (DataAccessException e){
            throw new DataAccessException("Unable to setup: " + e.getMessage());
        }
    }

    @AfterEach
    public void teardown() throws DataAccessException{
        try{
            authDataSQL.clearAllAuthData();
        }
        catch (DataAccessException e){
            throw new DataAccessException("Unable to tear down: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("AuthData Inserted Successfully")
    public void insertAuthSuccess() throws DataAccessException {
        AuthData authData = new AuthData("authToken123", "nicholasUsername");
        SQLAuthDataDAO sqlAuthDataDAO = new SQLAuthDataDAO();

        sqlAuthDataDAO.createAuth(authData);
        Map<String, AuthData> authDataList = sqlAuthDataDAO.getAllAuthData();

        AuthData expectedResult = authDataList.get("authToken123");

        Assertions.assertNotNull(authDataList, "authData list is empty");
        Assertions.assertEquals("authToken123", expectedResult.authToken(),
                "AuthTokens do not match");
        Assertions.assertEquals("nicholasUsername", expectedResult.username(),
                "Usernames do not match");
    }

    @Test
    @DisplayName("AuthData Inserted Incorrectly")
    public void insertAuthFail() throws DataAccessException{
        AuthData authData1 = new AuthData("123", "nicholasUsername");
        AuthData authData2 = new AuthData("123", "nicholasUsername");
        SQLAuthDataDAO sqlAuthDataDAO = new SQLAuthDataDAO();

        sqlAuthDataDAO.createAuth(authData1);
        Map<String, AuthData> authDataList = sqlAuthDataDAO.getAllAuthData();

        Assertions.assertNotNull(authDataList, "authData list is empty");
        Assertions.assertEquals(authData1, authDataList.get("123"), "authData list" +
                "does not match expected");

        DataAccessException exception = assertThrows(DataAccessException.class, () ->
                sqlAuthDataDAO.createAuth(authData2));

        Assertions.assertEquals("Unable to insert auth data: Duplicate entry '123' for key 'authdata.PRIMARY'", exception.getMessage(), "assertion" +
                "should raise an exception message");
    }

    @Test
    @DisplayName("Get authData Successfully")
    public void getAuthSuccess() throws DataAccessException{
        AuthData authData = new AuthData("12345", "nicholasUsername");
        SQLAuthDataDAO sqlAuthDataDAO = new SQLAuthDataDAO();

        sqlAuthDataDAO.createAuth(authData);
        AuthData result = sqlAuthDataDAO.getAuth(authData.authToken());

        Assertions.assertNotNull(result, "Returned auth data should not be null");
        Assertions.assertEquals("12345", result.authToken(), "auth tokens should match");
        Assertions.assertEquals("nicholasUsername", result.username(), "usernames should match");
    }

    @Test
    @DisplayName("Get authData Fails")
    public void getAuthFail() throws DataAccessException{
        AuthData authData = new AuthData("12345", "nicholasUsername");
        SQLAuthDataDAO sqlAuthDataDAO = new SQLAuthDataDAO();

        sqlAuthDataDAO.createAuth(authData);
        DataAccessException exception = assertThrows(DataAccessException.class, () ->
                sqlAuthDataDAO.getAuth("123"));

        Assertions.assertEquals("AuthToken is not found in the database", exception.getMessage(),
                "exception should result in a message");
    }

    @Test
    @DisplayName("Delete authData succeeds")
    public void deleteAuthSuccess() throws DataAccessException{
        AuthData authData = new AuthData("authToken123", "nicholasUsername");
        SQLAuthDataDAO sqlAuthDataDAO = new SQLAuthDataDAO();

        sqlAuthDataDAO.createAuth(authData);
        sqlAuthDataDAO.deleteAuth(authData.authToken());

        DataAccessException exception = assertThrows(DataAccessException.class, () ->
                sqlAuthDataDAO.getAuth(authData.authToken()));

        Assertions.assertEquals("AuthToken is not found in the database", exception.getMessage(),
                "exception should raise a message");
    }

    @Test
    @DisplayName("Getting multiple authData succeeds")
    public void getMultipleAuthDataPass() throws DataAccessException{
        AuthData authData1 = new AuthData("authToken123", "nicholasUsername");
        AuthData authData2 = new AuthData("12345", "userUserName");
        SQLAuthDataDAO sqlAuthDataDAO = new SQLAuthDataDAO();

        sqlAuthDataDAO.createAuth(authData1);
        sqlAuthDataDAO.createAuth(authData2);

        Map<String, AuthData> authDataList = sqlAuthDataDAO.getAllAuthData();

        Assertions.assertNotNull(authDataList,"authData should not be null");
        Assertions.assertEquals(2, authDataList.size(), "the expected number of elements" +
                "are not allotted to");
    }

    @Test
    @DisplayName("Clearing multiple data succeeds")
    public void clearMultipleAuthData() throws DataAccessException{
        AuthData authData1 = new AuthData("authToken123", "nicholasUsername");
        AuthData authData2 = new AuthData("12345", "userUserName");
        SQLAuthDataDAO sqlAuthDataDAO = new SQLAuthDataDAO();

        sqlAuthDataDAO.createAuth(authData1);
        sqlAuthDataDAO.createAuth(authData2);

        sqlAuthDataDAO.clearAllAuthData();

        Map<String, AuthData> authDataList = sqlAuthDataDAO.getAllAuthData();

        Assertions.assertTrue(authDataList.isEmpty(), "No authData should be returned");
    }
}
