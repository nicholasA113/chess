package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import service.httphandlers.*;
import spark.*;

import java.util.Map;

public class Server {

    UserDataAccess userDataAccess;
    AuthDataAccess authDataAccess;
    GameDataAccess gameDataAccess;

    UserService userService;
    GameService gameService;
    ClearService clearService;

    Gson serializer = new Gson();

    public void setUserDataAccess(UserDataAccess userDataAccess){
        this.userDataAccess = userDataAccess;
        if (this.authDataAccess != null) {
            this.userService = new UserService(authDataAccess, userDataAccess);
        }
    }

    public void setAuthDataAccess(AuthDataAccess authDataAccess){
        this.authDataAccess = authDataAccess;
        if (this.userDataAccess != null) {
            this.userService = new UserService(authDataAccess, userDataAccess);
        }
    }

    public void setGameDataAccess(GameDataAccess gameDataAccess){
        this.gameDataAccess = gameDataAccess;
        if (this.authDataAccess != null) {
            this.gameService = new GameService(authDataAccess, gameDataAccess);
        }
    }

    public void setClearService(AuthDataAccess authDataAccess, UserDataAccess userDataAccess,
                                GameDataAccess gameDataAccess){
        this.clearService = new ClearService(authDataAccess, userDataAccess, gameDataAccess);
    }

    public int run(int desiredPort) {
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

        Spark.get("/game", (request, response) -> {
            try{return new ListGamesHandler().listGamesHandler(request, response, serializer, 
                    authDataAccess, gameDataAccess, gameService);
            }
            catch (InvalidUsernameException e){
                response.status(401);
                return serializer.toJson(Map.of("message", "Error: Invalid username or authToken"));
            }
        });

        Spark.delete("/db", (request, response) ->{
            try{return new ClearHandler().clearHandler(request, response, serializer, 
                    userDataAccess, authDataAccess, gameDataAccess, clearService);
            }
            catch (InvalidAccessException e){
                response.status(500);
                return serializer.toJson(Map.of("message", "Error: not all data was cleared"));
            }
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
