package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import service.httphandlers.*;
import spark.*;

public class Server {

    SQLUserDataDAO sqlUserDataDAO;
    SQLAuthDataDAO sqlAuthDataDAO;
    SQLGameDataDAO sqlGameDataDAO;

    UserDataAccess userDataAccess;
    AuthDataAccess authDataAccess;
    GameDataAccess gameDataAccess;

    UserService userService;
    GameService gameService;
    ClearService clearService;

    Gson serializer = new Gson();

    public void setUserDataAccess(UserDataAccess userDataAccess){
        this.userDataAccess = userDataAccess;
    }

    public void setAuthDataAccess(AuthDataAccess authDataAccess){
        this.authDataAccess = authDataAccess;
    }

    public void setGameDataAccess(GameDataAccess gameDataAccess){
        this.gameDataAccess = gameDataAccess;
        if (this.authDataAccess != null) {
            this.gameService = new GameService(authDataAccess, gameDataAccess);
        }
        if (this.authDataAccess != null && this.userDataAccess != null){
            this.userService = new UserService(authDataAccess, userDataAccess);
        }
    }

    public void setClearService(AuthDataAccess authDataAccess, UserDataAccess userDataAccess,
                                GameDataAccess gameDataAccess){
        this.clearService = new ClearService(authDataAccess, userDataAccess, gameDataAccess);
    }

    public int run(int desiredPort) {

        try{
            sqlUserDataDAO = new SQLUserDataDAO();
            sqlAuthDataDAO = new SQLAuthDataDAO();
            sqlGameDataDAO = new SQLGameDataDAO();
        }
        catch (Exception e){
            System.out.println("Error getting sqlUSerDataDAO");
        }

        setUserDataAccess(sqlUserDataDAO);
        setAuthDataAccess(sqlAuthDataDAO);
        setGameDataAccess(sqlGameDataDAO);
        setClearService(sqlAuthDataDAO, sqlUserDataDAO, sqlGameDataDAO);

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        Spark.post("/user", (request, response) ->
                new RegisterHandler().registerHandler(request, response, serializer, userService)
        );

        Spark.post("/session", (request, response) ->
            new LoginHandler().loginHandler(request, response, serializer, userService)
        );

        Spark.delete("/session", (request, response) ->
           new LogoutHandler().logoutHandler(request, response, serializer,
                    userService)
        );

        Spark.post("/game", (request, response) ->
                new CreateGameHandler().createGameHandler(request, response,
                    serializer, gameService)
        );

        Spark.put("/game", (request, response) ->
            new JoinGameHandler().joinGameHandler(request, response, serializer,
                   gameService)
            );

        Spark.get("/game", (request, response) ->
            new ListGamesHandler().listGamesHandler(request, response, serializer,
                    gameService)
            );

        Spark.delete("/db", (request, response) ->
            new ClearHandler().clearHandler(request, response, serializer,
                    clearService)
        );

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
