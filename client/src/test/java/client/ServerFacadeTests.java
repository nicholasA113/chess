package client;

import exceptions.ResponseException;
import dataaccess.DataAccessException;
import dataaccess.SQLAuthDataDAO;
import dataaccess.SQLGameDataDAO;
import dataaccess.SQLUserDataDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import requestresultrecords.*;
import server.Server;
import server.ServerFacade;

import java.util.List;

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
        Assertions.assertEquals("An error message", exception.getMessage());
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

        Assertions.assertEquals("An error message", exception.getMessage());
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
    public void logoutUserTwice(){}

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
    public void createGameNoName(){}

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
        RequestResult.ListGamesResult games = facade.listGames(listGamesRequest.authToken());

        Assertions.assertNotNull(games);
        Assertions.assertEquals(1, games.size());
    }

}
