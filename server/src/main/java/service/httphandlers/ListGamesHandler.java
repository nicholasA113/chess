package service.httphandlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.GameService;
import requestresultrecords.RequestResult;
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
            return serializer.toJson(Map.of("message", "Error: No games exist with given authToken"));
        }
        response.status(200);
        return serializer.toJson(listGamesResult);
    }
}
