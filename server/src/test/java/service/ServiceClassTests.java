package service;

import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServiceClassTests {

    RequestResult.RegisterRequest registerRequest =
            new RequestResult.RegisterRequest("username123",
                    "password123", "email@email.com");
    RequestResult.LoginRequest loginRequest =
            new RequestResult.LoginRequest("username123", "password123");

    private UserService userServiceInstance;
    private GameService gameServiceInstance;
    private ClearService clearServiceInstance;

    private UserDataDAO userDataDAO;
    private AuthDataDAO authDataDAO;
    private GameDataDAO gameDataDAO;

    @BeforeEach
    public void setup(){
        userDataDAO = new UserDataDAO();
        authDataDAO = new AuthDataDAO();
        gameDataDAO = new GameDataDAO();

        userDataDAO.clearAllUserData();
        authDataDAO.clearAllAuthData();
        gameDataDAO.clearAllGameData();

        userServiceInstance = new UserService(authDataDAO, userDataDAO);
        gameServiceInstance = new GameService(authDataDAO, gameDataDAO);
        clearServiceInstance = new ClearService(authDataDAO, userDataDAO, gameDataDAO);
    }

    @AfterEach
    public void teardown() {
        userDataDAO.clearAllUserData();
        authDataDAO.clearAllAuthData();
        gameDataDAO.clearAllGameData();
    }

    @Test
    @DisplayName("Register Endpoint Valid Inputs")
    public void registerEndpointValid() throws DataAccessException {
        /** When **/
        RequestResult.RegisterResult actualRegisterResult =
                userServiceInstance.registerUser(registerRequest);

        /** Then **/
        Assertions.assertNotNull(actualRegisterResult, "The register result should not be null");
        Assertions.assertEquals("username123", actualRegisterResult.username(), "Usernames should be the same");
        Assertions.assertNotNull(actualRegisterResult.authToken(), "AuthToken should not be null");
    }

    @Test
    @DisplayName("Register Endpoint Invalid Inputs")
    public void registerEndpointInvalid(){
        /**  Given **/
        var registerRequestNull = new RequestResult.RegisterRequest(
                null, null, "123456@gmail.com");

        /** When **/
        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                userServiceInstance.registerUser(registerRequestNull));

        /** Then **/
        Assertions.assertEquals("Username, password, or email must not be null", exception.getMessage(),
                "Exception message should indicate one of the one of the fields is missing");
    }

    @Test
    @DisplayName("User already taken")
    public void registerUserTaken() throws DataAccessException {
        /** Given**/
        var registerRequestUserTaken = new RequestResult.RegisterRequest(
                "username123", "Password123", "nicholas@gmail.com");

        /** When **/
        RequestResult.RegisterResult registerResult1 = userServiceInstance.registerUser(registerRequest);
        InvalidUsernameException exception = assertThrows(InvalidUsernameException.class, () ->
                userServiceInstance.registerUser(registerRequestUserTaken));

        /** Then **/
        Assertions.assertNotNull(registerResult1, "First input should not be null");
        Assertions.assertEquals("Username is already taken", exception.getMessage(),
                "Error message should indicate that the username is already taken");
    }

    @Test
    @DisplayName("Login with valid credentials")
    public void loginValidInputs() throws DataAccessException{

        /** When **/
        RequestResult.RegisterResult registerResult = userServiceInstance.registerUser(registerRequest);
        RequestResult.LoginResult loginResult = userServiceInstance.login(loginRequest);

        /** Then **/
        Assertions.assertNotNull(registerResult, "Register Result should not be null");
        Assertions.assertNotNull(loginResult, "Login Result should not be null");
        Assertions.assertEquals("username123", loginResult.username(), "Usernames should match");
        Assertions.assertNotNull(loginResult.authToken(), "Auth Token should not be null");
    }

    @Test
    @DisplayName("Login with invalid password")
    public void loginInvalidPassword() throws DataAccessException{
        /** Given **/
        var loginRequestInvalidPassword = new RequestResult.LoginRequest("username123",
                "123");

        /** When **/
        RequestResult.RegisterResult registerResult = userServiceInstance.registerUser(registerRequest);
        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                userServiceInstance.login(loginRequestInvalidPassword));

        /** Then **/
        Assertions.assertNotNull(registerResult, "Register Result should not be null");
        Assertions.assertEquals("Password does not match", exception.getMessage(),
                "Error message should indicate that passwords do not match");
    }

    @Test
    @DisplayName("Login with invalid username")
    public void loginInvalidUsername() throws DataAccessException{
        /** Given **/
        var loginRequestInvalidUsername = new RequestResult.LoginRequest("nicholas",
                "123456");

        /** When **/
        RequestResult.RegisterResult registerResult = userServiceInstance.registerUser(registerRequest);
        InvalidUsernameException exception = assertThrows(InvalidUsernameException.class, () ->
                userServiceInstance.login(loginRequestInvalidUsername));

        /** Then **/
        Assertions.assertNotNull(registerResult, "Register Result should not be null");
        Assertions.assertEquals("Username is not found or incorrect", exception.getMessage(),
                "Error message should indicate that username does not match");
    }

    @Test
    @DisplayName("Logout with valid authToken")
    public void logoutValidToken() throws DataAccessException{

        /** When **/
        RequestResult.RegisterResult registerResult = userServiceInstance.registerUser(registerRequest);
        RequestResult.LoginResult loginResult = userServiceInstance.login(loginRequest);

        String authToken = loginResult.authToken();

        RequestResult.LogoutResult logoutResult = userServiceInstance.logout(authToken);

        /** Then **/
        Assertions.assertNotNull(registerResult, "Register Result should not be null");
        Assertions.assertNotNull(loginResult, "Login result should not be null");
        Assertions.assertNull(logoutResult, "Logout result should return null");
    }

    @Test
    @DisplayName("Logout with invalid authToken")
    public void logoutInvalidToken() throws DataAccessException{

        /** When **/
        RequestResult.RegisterResult registerResult = userServiceInstance.registerUser(registerRequest);
        RequestResult.LoginResult loginResult = userServiceInstance.login(loginRequest);

        String authToken = loginResult.authToken()+1;

        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                userServiceInstance.logout(authToken));

        /** Then **/
        Assertions.assertNotNull(registerResult, "Register Result should not be null");
        Assertions.assertNotNull(loginResult, "Login result should not be null");
        Assertions.assertEquals("AuthToken is invalid", exception.getMessage(),
                "Error message should indicate that authToken is invalid");
    }

    @Test
    @DisplayName("Create game with authToken and gameName")
    public void createGameValidInput() throws DataAccessException{
        /** When **/
        RequestResult.RegisterResult registerResult = userServiceInstance.registerUser(
                registerRequest);
        RequestResult.LoginResult loginResult = userServiceInstance.login(
                loginRequest);

        String authToken = loginResult.authToken();

        RequestResult.CreateGameRequest createGameRequest = new RequestResult.CreateGameRequest(
                authToken, "Game Name Test");

        RequestResult.CreateGameResult createGameResult = gameServiceInstance.createGame(
                createGameRequest, authToken);

        /** Then **/
        Assertions.assertNotNull(registerResult, "Register result should not be null");
        Assertions.assertNotEquals(0, createGameResult.gameID(),
                "Game ID should not be null");
        Assertions.assertNotNull(createGameResult, "Result should not be null");
    }

    @Test
    @DisplayName("Create game with no authToken")
    public void createGameNoAuthToken(){
        RequestResult.CreateGameRequest createGameRequest = new RequestResult.CreateGameRequest(
                null, "Game Name Test");
        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                gameServiceInstance.createGame(createGameRequest, null));

        Assertions.assertEquals("AuthToken and/or Game Name is invalid or empty", exception.getMessage(),
                "Error should indicate an authToken is needed");
    }

    @Test
    @DisplayName("Create game with no game name")
    public void createGameNoName() throws DataAccessException{
        userServiceInstance.registerUser(registerRequest);
        RequestResult.LoginResult loginResult = userServiceInstance.login(loginRequest);

        String authToken = loginResult.authToken()+1;

        RequestResult.CreateGameRequest createGameRequest = new RequestResult.CreateGameRequest(
                authToken, "Game Name Test");

        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                gameServiceInstance.createGame(createGameRequest, authToken));

        Assertions.assertEquals("AuthToken and/or Game Name is invalid or empty", exception.getMessage(),
                "Error should indicate auth tokens do not match");
    }

    @Test
    @DisplayName("Join Game valid inputs")
    public void joinGameValidInputs() throws DataAccessException{
        userServiceInstance.registerUser(registerRequest);
        RequestResult.LoginResult loginResult = userServiceInstance.login(loginRequest);

        String authToken = loginResult.authToken();
        RequestResult.CreateGameRequest createGameRequest = new RequestResult.CreateGameRequest(
                authToken, "Chess Game #1");

        RequestResult.CreateGameResult createGameResult = gameServiceInstance.createGame(
                createGameRequest, authToken);

        RequestResult.JoinGameRequest joinGameRequest = new RequestResult.JoinGameRequest(authToken,
                "WHITE", createGameResult.gameID());

        RequestResult.JoinGameResult joinGameResult = gameServiceInstance.joinGame(
                joinGameRequest, authToken);

        GameData game = gameDataDAO.getGame(joinGameRequest.gameID());
        Assertions.assertNotNull(joinGameResult, "Join game result should not be null");
        Assertions.assertNull(game.blackUsername(), "Black username should be null in this instance");
        Assertions.assertEquals(loginResult.username(), game.whiteUsername(), "White username should match");
    }

    @Test
    @DisplayName("Join game result invalid inputs")
    public void joinGameInvalid() throws DataAccessException{
        userServiceInstance.registerUser(registerRequest);
        RequestResult.LoginResult loginResult = userServiceInstance.login(loginRequest);

        RequestResult.RegisterRequest registerRequest2 = new RequestResult.RegisterRequest("username1234", "password123",
                "email@email.com");
        RequestResult.LoginRequest loginRequest2 = new RequestResult.LoginRequest("username1234",
                "password123");

        userServiceInstance.registerUser(registerRequest2);
        RequestResult.LoginResult loginResult2 = userServiceInstance.login(loginRequest2);

        String authToken2 = loginResult2.authToken();
        String authToken = loginResult.authToken();

        RequestResult.CreateGameRequest createGameRequest = new RequestResult.CreateGameRequest(
                authToken, "Chess Game #1");

        RequestResult.CreateGameResult createGameResult = gameServiceInstance.createGame(
                createGameRequest, authToken);


        RequestResult.JoinGameRequest joinGameRequest1 = new RequestResult.JoinGameRequest(authToken,
                "WHITE", createGameResult.gameID());

        RequestResult.JoinGameResult joinGameResult1 = gameServiceInstance.joinGame(
                joinGameRequest1, authToken);

        RequestResult.JoinGameRequest joinGameRequest2 = new RequestResult.JoinGameRequest(authToken2,
                "WHITE", createGameResult.gameID());


        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                gameServiceInstance.joinGame(joinGameRequest2, authToken2));

        Assertions.assertNotNull(joinGameResult1, "First join game should not be null");
        Assertions.assertEquals("Both users are already taken for this game", exception.getMessage(),
                "Error should indicate an authToken is needed");
    }

    @Test
    @DisplayName("List all games")
    public void listGamesSuccess() throws DataAccessException{
        userServiceInstance.registerUser(registerRequest);
        RequestResult.LoginResult loginResult = userServiceInstance.login(
                loginRequest);

        String authToken = loginResult.authToken();

        RequestResult.CreateGameRequest createGameRequest = new RequestResult.CreateGameRequest(
                authToken, "Game Name Test");
        RequestResult.CreateGameRequest createGameRequest2 = new RequestResult.CreateGameRequest(
                authToken, "Game Name Test2");

        RequestResult.CreateGameResult createGameResult = gameServiceInstance.createGame(
                createGameRequest, authToken);
        RequestResult.CreateGameResult createGameResult2 = gameServiceInstance.createGame(
                createGameRequest2, authToken);

        RequestResult.JoinGameRequest joinGameRequest1 = new RequestResult.JoinGameRequest(authToken,
                "WHITE", createGameResult.gameID());
        RequestResult.JoinGameRequest joinGameRequest2 = new RequestResult.JoinGameRequest(authToken,
                "WHITE", createGameResult2.gameID());

        gameServiceInstance.joinGame(
                joinGameRequest1, authToken);
        gameServiceInstance.joinGame(
                joinGameRequest2, authToken);

        RequestResult.ListGamesResult listGamesResult = gameServiceInstance.listGames(authToken);

        Assertions.assertNotNull(listGamesResult, "Games should be listed");
    }

    @Test
    @DisplayName("List games but empty")
    public void listGamesInvalidAuthToken() throws DataAccessException{
        userServiceInstance.registerUser(registerRequest);
        RequestResult.LoginResult loginResult = userServiceInstance.login(
                loginRequest);

        String authToken = loginResult.authToken()+1;

        InvalidUsernameException exception = assertThrows(InvalidUsernameException.class, () ->
                gameServiceInstance.listGames(authToken));

        Assertions.assertEquals("Username/AuthToken is not valid",
                exception.getMessage(), "Message should indicate that the username/authToken is invalid");
    }

    @Test
    @DisplayName("ClearGamesSuccessful")
    public void clearGamesSuccess()throws DataAccessException{
        userServiceInstance.registerUser(registerRequest);
        RequestResult.LoginResult loginResult = userServiceInstance.login(loginRequest);

        String authToken = loginResult.authToken();

        RequestResult.CreateGameRequest createGameRequest = new RequestResult.CreateGameRequest(
                authToken, "Game Name Test");
        RequestResult.CreateGameResult createGameResult = gameServiceInstance.createGame(
                createGameRequest, authToken);

        RequestResult.JoinGameRequest joinGameRequest = new RequestResult.JoinGameRequest(
                authToken, "WHITE", createGameResult.gameID());
        gameServiceInstance.joinGame(joinGameRequest, authToken);

        RequestResult.ClearDataRequest clearDataRequest = new RequestResult.ClearDataRequest();
        clearServiceInstance.clearData(clearDataRequest);

        Assertions.assertTrue(userDataDAO.getAllUserData().isEmpty(), "User data should be empty");
        Assertions.assertTrue(authDataDAO.getAllAuthData().isEmpty(), "Auth data should be empty");
        Assertions.assertTrue(gameDataDAO.getAllGames().isEmpty(), "Game data should be empty");
    }


}
