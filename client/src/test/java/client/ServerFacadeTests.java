package client;

import dataaccess.DataAccessException;
import dataaccess.SQLAuthDataDAO;
import dataaccess.SQLGameDataDAO;
import dataaccess.SQLUserDataDAO;
import model.AuthData;
import org.junit.jupiter.api.*;
import requestresultrecords.*;
import server.Server;
import server.ServerFacade;


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
    public void registerSuccess() {
        RequestResult.RegisterRequest request = new RequestResult.RegisterRequest(
                "nicholasUsername", "password123", "email@email.com");
        AuthData authData = facade.register(request);
        Assertions.assertTrue(authData.authToken().length() > 10);
    }

}
