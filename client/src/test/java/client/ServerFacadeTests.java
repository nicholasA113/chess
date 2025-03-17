package client;

import exceptions.ResponseException;
import dataaccess.DataAccessException;
import dataaccess.SQLAuthDataDAO;
import dataaccess.SQLGameDataDAO;
import dataaccess.SQLUserDataDAO;
import model.AuthData;
import org.junit.jupiter.api.*;
import requestresultrecords.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    private SQLAuthDataDAO authDataSQL;
    private SQLUserDataDAO userDataSQL;
    private SQLGameDataDAO gameDataSQL;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

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

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void registerSuccess() throws Exception {
        RequestResult.RegisterRequest request = new RequestResult.RegisterRequest(
                "nicholasUsername", "password123", "email@email.com");
        AuthData authData = facade.register(request);
        Assertions.assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void registerExistingUser() throws Exception {
        RequestResult.RegisterRequest request = new RequestResult.RegisterRequest(
                "nicholasUsername", "password123", "email@email.com");
        AuthData authData = facade.register(request);

        Exception exception = assertThrows(ResponseException.class, () ->
                    facade.register(request));

        Assertions.assertNotNull(authData.authToken());
        Assertions.assertEquals("Error: Username is already taken", exception.getMessage());
    }

    @Test
    public void loginSuccess() throws Exception {
        RequestResult.RegisterRequest request = new RequestResult.RegisterRequest(
                "nicholasUsername", "password123", "email@email.com");
        AuthData authData = facade.register(request);

        RequestResult.LoginRequest loginRequest = new RequestResult.LoginRequest(
                "nicholasUsername", "password123");
        RequestResult.LoginResult loginResult = facade.login(loginRequest);

        Assertions.assertNotNull(authData);
        Assertions.assertNotNull(loginResult);
        Assertions.assertTrue(loginResult.authToken().length() > 10);
    }

    @Test
    public void loginUnregisteredUser() {
        RequestResult.LoginRequest loginRequest = new RequestResult.LoginRequest(
                "nicholasUsername", "password123");

        Exception exception = assertThrows(ResponseException.class, () ->
                facade.login(loginRequest));

        Assertions.assertEquals("Error: Invalid credentials", exception.getMessage());
    }

    @Test
    public void logoutSuccess() throws Exception {
        RequestResult.RegisterRequest request = new RequestResult.RegisterRequest(
                "nicholasUsername", "password123", "email@email.com");
        AuthData authData = facade.register(request);

        RequestResult.LoginRequest loginRequest = new RequestResult.LoginRequest(
                "nicholasUsername", "password123");
        facade.login(loginRequest);

        RequestResult.LogoutRequest logoutRequest = new RequestResult.LogoutRequest(
                authData.authToken());

        RequestResult.LogoutResult result = facade.logout(logoutRequest.authToken());

        Assertions.assertNull(result);
    }

    @Test
    public void logoutUserTwice() throws Exception{
        RequestResult.RegisterRequest request = new RequestResult.RegisterRequest(
                "nicholasUsername", "password123", "email@email.com");
        AuthData authData = facade.register(request);

        RequestResult.LoginRequest loginRequest = new RequestResult.LoginRequest(
                "nicholasUsername", "password123");
        facade.login(loginRequest);

        RequestResult.LogoutRequest logoutRequest = new RequestResult.LogoutRequest(
                authData.authToken());
        facade.logout(logoutRequest.authToken());

        Exception exception = assertThrows(ResponseException.class, () ->
                facade.logout(logoutRequest.authToken()));

        Assertions.assertEquals("Error: AuthToken is invalid", exception.getMessage());
    }

    @Test
    public void createGameSuccess() throws Exception {
        RequestResult.RegisterRequest request = new RequestResult.RegisterRequest(
                "nicholasUsername", "password123", "email@email.com");
        facade.register(request);
        RequestResult.LoginRequest loginRequest = new RequestResult.LoginRequest(
                "nicholasUsername", "password123");
        RequestResult.LoginResult loginResult = facade.login(loginRequest);

        RequestResult.CreateGameRequest createGameRequest = new RequestResult.CreateGameRequest(
                loginResult.authToken(), "myGameName");
        RequestResult.CreateGameResult createGameResult = facade.createGame(
                createGameRequest.authToken(), createGameRequest.gameName());

        Assertions.assertNotEquals(0, createGameResult.gameID());
    }

    @Test
    public void createGameNoName() throws Exception {
        RequestResult.RegisterRequest request = new RequestResult.RegisterRequest(
                "nicholasUsername", "password123", "email@email.com");
        facade.register(request);
        RequestResult.LoginRequest loginRequest = new RequestResult.LoginRequest(
                "nicholasUsername", "password123");
        RequestResult.LoginResult loginResult = facade.login(loginRequest);

        RequestResult.CreateGameRequest createGameRequest = new RequestResult.CreateGameRequest(
                loginResult.authToken(), null);

        Exception exception = assertThrows(Exception.class, () ->
                facade.createGame(createGameRequest.authToken(), createGameRequest.gameName()));

        Assertions.assertEquals("Given input(s) is/are null", exception.getMessage());
    }

    @Test
    public void joinGameSuccess() throws Exception {
        RequestResult.RegisterRequest request = new RequestResult.RegisterRequest(
                "nicholasUsername", "password123", "email@email.com");
        facade.register(request);
        RequestResult.LoginRequest loginRequest = new RequestResult.LoginRequest(
                "nicholasUsername", "password123");
        RequestResult.LoginResult loginResult = facade.login(loginRequest);

        RequestResult.CreateGameRequest createGameRequest = new RequestResult.CreateGameRequest(
                loginResult.authToken(), "myGameName");
        RequestResult.CreateGameResult createGameResult = facade.createGame(
                createGameRequest.authToken(), createGameRequest.gameName());

        Assertions.assertDoesNotThrow(() -> facade.joinGame(loginResult.authToken(),
                "WHITE",
                createGameResult.gameID()));
    }

    @Test
    public void joinGameNoneExist() throws Exception {
        RequestResult.RegisterRequest request = new RequestResult.RegisterRequest(
                "nicholasUsername", "password123", "email@email.com");
        facade.register(request);
        RequestResult.LoginRequest loginRequest = new RequestResult.LoginRequest(
                "nicholasUsername", "password123");
        RequestResult.LoginResult loginResult = facade.login(loginRequest);

        Exception exception = assertThrows(ResponseException.class, () ->
                facade.joinGame(loginResult.authToken(), "BLACK", 1));

        Assertions.assertEquals("Error: invalid data was passed in", exception.getMessage());
    }

    @Test
    public void listGamesSuccess() throws Exception{
        RequestResult.RegisterRequest request = new RequestResult.RegisterRequest(
                "nicholasUsername", "password123", "email@email.com");
        facade.register(request);
        RequestResult.LoginRequest loginRequest = new RequestResult.LoginRequest(
                "nicholasUsername", "password123");
        RequestResult.LoginResult loginResult = facade.login(loginRequest);
        RequestResult.CreateGameRequest createGameRequest = new RequestResult.CreateGameRequest(
                loginResult.authToken(), "myGameName");
        facade.createGame(createGameRequest.authToken(), createGameRequest.gameName());

        RequestResult.ListGamesRequest listGamesRequest = new RequestResult.ListGamesRequest(
                loginResult.authToken());
        RequestResult.ListGamesResult allGames = facade.listGames(listGamesRequest.authToken());

        Assertions.assertNotNull(allGames);
        Assertions.assertEquals(1, allGames.games().size());
    }

    @Test
    public void listGamesNoneExist() throws Exception {
        RequestResult.RegisterRequest request = new RequestResult.RegisterRequest(
                "nicholasUsername", "password123", "email@email.com");
        facade.register(request);
        RequestResult.LoginRequest loginRequest = new RequestResult.LoginRequest(
                "nicholasUsername", "password123");
        RequestResult.LoginResult loginResult = facade.login(loginRequest);

        RequestResult.ListGamesRequest listGamesRequest = new RequestResult.ListGamesRequest(
                loginResult.authToken());
        String authToken = listGamesRequest.authToken();
        Exception exception = assertThrows(ResponseException.class, () ->
                facade.listGames(authToken+1));

        Assertions.assertEquals("Error: No games exist with given authToken", exception.getMessage());
    }

    @Test
    public void clearDataSuccess() throws Exception {
        RequestResult.RegisterRequest request = new RequestResult.RegisterRequest(
                "nicholasUsername", "password123", "email@email.com");
        facade.register(request);
        RequestResult.LoginRequest loginRequest = new RequestResult.LoginRequest(
                "nicholasUsername", "password123");
        RequestResult.LoginResult loginResult = facade.login(loginRequest);
        RequestResult.CreateGameRequest createGameRequest = new RequestResult.CreateGameRequest(
                loginResult.authToken(), "myGameName");
        facade.createGame(createGameRequest.authToken(), createGameRequest.gameName());

        facade.clearData();

        RequestResult.ListGamesRequest listGamesRequest = new RequestResult.ListGamesRequest(
                loginResult.authToken());

        Exception listGamesException = assertThrows(ResponseException.class, () ->
                facade.listGames(listGamesRequest.authToken()));
        Exception exception = assertThrows(ResponseException.class,() ->
                facade.login(loginRequest));

        Assertions.assertEquals("Error: No games exist with given authToken", listGamesException.getMessage());
        Assertions.assertEquals("Error: Invalid credentials", exception.getMessage());
    }
}
