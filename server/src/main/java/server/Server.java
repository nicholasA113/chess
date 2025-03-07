package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import service.httphandlers.*;
import spark.*;

import java.util.Map;

import dataaccess.DatabaseManager;

public class Server {

    UserDataDAO userDataDAO = new UserDataDAO();
    AuthDataDAO authDataDAO = new AuthDataDAO();
    GameDataDAO gameDataDAO = new GameDataDAO();

    UserService userService = new UserService();
    GameService gameService = new GameService();
    ClearService clearService = new ClearService();

    Gson serializer = new Gson();

    public int run(int desiredPort) {

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        Spark.post("/user", (request, response) -> {
            try{return new RegisterHandler().registerHandler(request, response, serializer, userDataDAO, authDataDAO, userService);
            }
            catch (InvalidUsernameException e){
                response.status(403);
                return serializer.toJson(Map.of("message", "Error: Username is already taken"));
            }
            catch (InvalidInputException e){
                response.status(400);
                return serializer.toJson(Map.of("message", "Error: Invalid inputs"));
            }
        });
        Spark.post("/session", (request, response) -> {
            try{return new LoginHandler().loginHandler(request, response, serializer, userDataDAO, authDataDAO, userService);
            }
            catch (InvalidUsernameException e){
                response.status(401);
                return serializer.toJson(Map.of("message", "Error: Invalid credentials"));
            }
            catch (InvalidInputException e){
                response.status(401);
                return serializer.toJson(Map.of("message", "Error: Passwords do not match"));
            }
        });
        Spark.delete("/session", (request, response) -> {
            try{return new LogoutHandler().logoutHandler(request, response, serializer, authDataDAO, userService);
            }
            catch (InvalidInputException e){
                response.status(401);
                return serializer.toJson(Map.of("message", "Error: AuthToken should not be null"));
            }
        });
        Spark.post("/game", (request, response) -> {
            try{return new CreateGameHandler().createGameHandler(request, response, serializer, authDataDAO, gameDataDAO, gameService);
            }
            catch (InvalidUsernameException e){
                response.status(400);
                return serializer.toJson(Map.of("message", "Error: Invalid username"));
            }
            catch (InvalidInputException e){
                response.status(401);
                return serializer.toJson(Map.of("message", "Error: AuthToken and/or Game Name is invalid or empty"));
            }
        });
        Spark.put("/game", (request, response) -> {
            try{return new JoinGameHandler().joinGameHandler(request, response, serializer, authDataDAO, gameDataDAO, gameService);
            }
            catch (InvalidUsernameException e){
                response.status(401);
                return serializer.toJson(Map.of("message", "Error: Invalid username"));
            }
            catch (InvalidInputException e){
                response.status(403);
                return serializer.toJson(Map.of("message", "Error: Usernames are already taken"));
            }
            catch (InvalidAccessException e){
                response.status(400);
                return serializer.toJson(Map.of("message", "Error: invalid playerColor requested"));
            }
        });
        Spark.get("/game", (request, response) -> {
            try{return new ListGamesHandler().listGamesHandler(request, response, serializer, authDataDAO, gameDataDAO, gameService);
            }
            catch (InvalidUsernameException e){
                response.status(401);
                return serializer.toJson(Map.of("message", "Error: Invalid username or authToken"));
            }
        });
        Spark.delete("/db", (request, response) ->{
            try{return new ClearHandler().clearHandler(request, response, serializer, userDataDAO, authDataDAO, gameDataDAO, clearService);
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
