package service.httphandlers;

import com.google.gson.Gson;
import dataaccess.AuthDataDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDataDAO;
import dataaccess.InvalidUsernameException;
import service.GameService;
import service.RequestResult;
import spark.Request;
import spark.Response;

import java.util.Map;

public class ListGamesHandler {

    RequestResult.ListGamesResult listGamesResult;

    public Object listGamesHandler(Request request, Response response, Gson serializer,
                                   GameService gameService){
        try {
            String authToken = request.headers("authorization");
            listGamesResult = gameService.listGames(authToken);
        }
        catch (DataAccessException e){
            response.status(401);
            return serializer.toJson(Map.of("message", "Error: Invalid username or authToken"));
        }
        response.status(200);
        return serializer.toJson(listGamesResult);
    }
}
