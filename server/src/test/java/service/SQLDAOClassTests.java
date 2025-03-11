package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SQLDAOClassTests {

    private SQLAuthDataDAO authDataSQL;
    private SQLUserDataDAO userDataSQL;
    private SQLGameDataDAO gameDataSQL;

    @BeforeEach
    public void setup() throws DataAccessException {
        try{
            authDataSQL = new SQLAuthDataDAO();
            authDataSQL.clearAllAuthData();

            userDataSQL = new SQLUserDataDAO();
            userDataSQL.clearAllUserData();

            gameDataSQL = new SQLGameDataDAO();
            gameDataSQL.clearAllGameData();
        }
        catch (DataAccessException e){
            throw new DataAccessException("Unable to setup: " + e.getMessage());
        }
    }

    @AfterEach
    public void teardown() throws DataAccessException{
        try{
            authDataSQL.clearAllAuthData();
            userDataSQL.clearAllUserData();
            gameDataSQL.clearAllGameData();
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

    @Test
    @DisplayName("Insert User Success")
    public void insertUserSuccess() throws DataAccessException{
        UserData userData = new UserData("nicholasUsername", "myPasswordIsSecret",
                "email@email.org");
        SQLUserDataDAO sqlUserDataDAO = new SQLUserDataDAO();

        sqlUserDataDAO.insertUser(userData);

        UserData storedUserData = sqlUserDataDAO.getUser(userData.username());

        Assertions.assertNotNull(storedUserData, "stored user data should contain something");
        Assertions.assertNotEquals(userData.password(), storedUserData.password(),
                "Stored password should be hashed");
    }

    @Test
    @DisplayName("Insert User Fail")
    public void insertUserFails() throws DataAccessException{
        UserData userData = new UserData(null, "myPasswordIsSecret",
                "email@email.org");
        SQLUserDataDAO sqlUserDataDAO = new SQLUserDataDAO();

        DataAccessException exception = assertThrows(DataAccessException.class, () ->
                sqlUserDataDAO.insertUser(userData));

        Assertions.assertEquals("Error inserting user into database: Column 'username' cannot be null", exception.getMessage(),
                "an error message should be raise");
    }

    @Test
    @DisplayName("Get User Success")
    public void getUserSuccess() throws DataAccessException{
        UserData userData1 = new UserData("nicholasUsername",
                "myPasswordIsSecure", "email@email.com");
        UserData userData2 = new UserData("myUsername",
                "myPassword", "my@email.com");
        SQLUserDataDAO sqlUserDataDAO = new SQLUserDataDAO();

        sqlUserDataDAO.insertUser(userData1);
        sqlUserDataDAO.insertUser(userData2);

        UserData result = sqlUserDataDAO.getUser(userData1.username());

        Assertions.assertNotNull(result, "result should not be null");
        Assertions.assertEquals(userData1.username(), result.username(),
                "Resulting username should match existing one");
    }

    @Test
    @DisplayName("Get user fails")
    public void getUserFails() throws DataAccessException{
        UserData userData1 = new UserData("nicholasUsername",
                "myPasswordIsSecure", "email@email.com");
        UserData userData2 = new UserData("myUsername",
                "myPassword", "my@email.com");
        SQLUserDataDAO sqlUserDataDAO = new SQLUserDataDAO();

        sqlUserDataDAO.insertUser(userData1);
        sqlUserDataDAO.insertUser(userData2);

        UserData user = sqlUserDataDAO.getUser("nicksusername");

        Assertions.assertNull(user, "User should return null");
    }

    @Test
    @DisplayName("Get all user succeeds")
    public void getAllUsersSucceeds() throws DataAccessException{
        UserData userData1 = new UserData("nickUsername",
                "passWord", "email@email.com");
        UserData userData2 = new UserData("nicholasUsername",
                "passWord", "email@emails.com");
        SQLUserDataDAO sqlUserDataDAO = new SQLUserDataDAO();

        sqlUserDataDAO.insertUser(userData1);
        sqlUserDataDAO.insertUser(userData2);

        ArrayList<UserData> userData = sqlUserDataDAO.getAllUserData();

        Assertions.assertNotNull(userData, "userData should not be null");
        boolean firstUser = false;
        boolean secondUser = false;
        for (UserData user : userData){
            if (user.username().equals("nickUsername")) firstUser = true;
            if (user.username().equals("nicholasUsername")) secondUser = true;

            Assertions.assertNotEquals("passWord", user.password(), "Passwords" +
                    "should not be matching");
        }
        Assertions.assertTrue(firstUser, "List should contain nickUsername");
        Assertions.assertTrue(secondUser, "List should contain nicholasUsername");
    }

    @Test
    @DisplayName("Get all users fails")
    public void getAllUsersNoData() throws DataAccessException{
        SQLUserDataDAO sqlUserDataDAO = new SQLUserDataDAO();

        ArrayList<UserData> userData = sqlUserDataDAO.getAllUserData();

        Assertions.assertTrue(userData.isEmpty(), "Expected empty user list but found users.");
    }

    @Test
    @DisplayName("Clear multiple data success")
    public void clearAllUsersSucceeds() throws DataAccessException{
        UserData userData1 = new UserData("nickUsername",
                "passWord", "email@email.com");
        UserData userData2 = new UserData("nicholasUsername",
                "passWord", "email@emails.com");
        SQLUserDataDAO sqlUserDataDAO = new SQLUserDataDAO();

        sqlUserDataDAO.insertUser(userData1);
        sqlUserDataDAO.insertUser(userData2);

        sqlUserDataDAO.clearAllUserData();

        ArrayList<UserData> userData = sqlUserDataDAO.getAllUserData();

        Assertions.assertTrue(userData.isEmpty(), "all userData should" +
                "be gone after calling the clear all function");
    }

    @Test
    @DisplayName("Create game succeeds")
    public void createGameSuccess() throws DataAccessException{
        SQLGameDataDAO sqlGameDataDAO = new SQLGameDataDAO();

        sqlGameDataDAO.createGame("myGameName");

        int resultID = sqlGameDataDAO.getID("myGameName");
        GameData result = sqlGameDataDAO.getGame(resultID);

        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertEquals(resultID, result.gameID(), "Result id's should match");
    }

    @Test
    @DisplayName("Create game fails")
    public void createGameAlreadyInDatabase() throws DataAccessException{
        SQLGameDataDAO sqlGameDataDAO = new SQLGameDataDAO();
        sqlGameDataDAO.createGame("myGameName");

        DataAccessException exception = assertThrows(DataAccessException.class, () ->
                sqlGameDataDAO.createGame("myGameName"));

        Assertions.assertEquals("gameName already exists in database", exception.getMessage(),
                "Assertion should throw a message");
    }

    @Test
    @DisplayName("Get game succeeds")
    public void getGameInDatabase() throws DataAccessException{
        SQLGameDataDAO sqlGameDataDAO = new SQLGameDataDAO();
        sqlGameDataDAO.createGame("myGameName");
        sqlGameDataDAO.createGame("myOtherName");

        int gameID = sqlGameDataDAO.getID("myGameName");
        GameData chessGameData = sqlGameDataDAO.getGame(gameID);

        Assertions.assertNotNull(chessGameData, "gameData should not return null");
        Assertions.assertEquals("myGameName", chessGameData.gameName(), "Game" +
                " name should match");
    }

    @Test
    @DisplayName("Get game fails")
    public void getGameFails(){
        SQLGameDataDAO sqlGameDataDAO = new SQLGameDataDAO();

        DataAccessException exception = assertThrows(DataAccessException.class, () ->
                sqlGameDataDAO.getGame(34));

        Assertions.assertEquals("Game is not found in the database", exception.getMessage(),
                "Assertion should throw a message");
    }

    @Test
    @DisplayName("Update game succeeds")
    public void updateGameSucceeds() throws DataAccessException{
        SQLGameDataDAO sqlGameDataDAO = new SQLGameDataDAO();
        sqlGameDataDAO.createGame("myGameName");
        int gameID = sqlGameDataDAO.getID("myGameName");
        GameData game = sqlGameDataDAO.getGame(gameID);

        sqlGameDataDAO.updateGame(new GameData(game.gameID(), "nicholasUsername",
                game.blackUsername(), game.gameName(), game.game()));
        GameData game2 = sqlGameDataDAO.getGame(gameID);

        Assertions.assertNotNull(game2, "Game2 should not return null");
        Assertions.assertNotEquals(game.whiteUsername(), game2.whiteUsername(),
                "Usernames should no longer match");
        Assertions.assertEquals("nicholasUsername", game2.whiteUsername(),
                "WhiteUsername should be updated");
    }

    @Test
    @DisplayName("Update game that doesn't exist")
    public void updateGameNotExists(){
        SQLGameDataDAO sqlGameDataDAO = new SQLGameDataDAO();

        DataAccessException exception = assertThrows(DataAccessException.class, () ->
                sqlGameDataDAO.updateGame(new GameData(12,
                        null, null,
                        "someName", new ChessGame())));

        Assertions.assertEquals("No game was found with given gameID", exception.getMessage(),
                "Assertion should throw a message");
    }

    @Test
    @DisplayName("Get gameID success")
    public void getGameIDSucceeds() throws DataAccessException {
        SQLGameDataDAO sqlGameDataDAO = new SQLGameDataDAO();
        sqlGameDataDAO.createGame("myGameName");

        int gameID = sqlGameDataDAO.getID("myGameName");

        GameData gameData = sqlGameDataDAO.getGame(gameID);

        Assertions.assertNotNull(gameData, "GameData should not return null");
        Assertions.assertEquals(gameID, gameData.gameID(), "gameID's should match");
    }

    @Test
    @DisplayName("Get gameID fails")
    public void getGameIDFails(){
        SQLGameDataDAO sqlGameDataDAO = new SQLGameDataDAO();

        DataAccessException exception = assertThrows(DataAccessException.class, () ->
                sqlGameDataDAO.getID("myGameName"));

        Assertions.assertEquals("ID not found in gameData", exception.getMessage(),
                "Assertion should throw a message");
    }

    @Test
    @DisplayName("Get all games succeeds")
    public void getAllGamesSucceeds() throws DataAccessException{
        SQLGameDataDAO sqlGameDataDAO = new SQLGameDataDAO();
        sqlGameDataDAO.createGame("myGameName");
        sqlGameDataDAO.createGame("myGameName2");

        ArrayList<GameData> gameDataList = sqlGameDataDAO.getAllGames();

        Assertions.assertNotNull(gameDataList, "GameDataList should not be null");
        Assertions.assertEquals(2, gameDataList.size(), "There should " +
                "be two games in the gameData list");
    }

    @Test
    @DisplayName("Get all games but none exist")
    public void getAllGamesNoneExist() throws DataAccessException{
        SQLGameDataDAO sqlGameDataDAO = new SQLGameDataDAO();
        ArrayList<GameData> gameDataList = sqlGameDataDAO.getAllGames();

        Assertions.assertTrue(gameDataList.isEmpty(), "gameDataList should be empty");
    }

    @Test
    @DisplayName("Clearing multiple games")
    public void clearAllGames() throws DataAccessException{
        SQLGameDataDAO sqlGameDataDAO = new SQLGameDataDAO();
        sqlGameDataDAO.createGame("myGameName");
        sqlGameDataDAO.createGame("myGameName2");

        sqlGameDataDAO.clearAllGameData();
        ArrayList<GameData> gameDataList = sqlGameDataDAO.getAllGames();

        Assertions.assertTrue(gameDataList.isEmpty(), "There should be no games " +
                "after calling the clear all method");
    }
}
