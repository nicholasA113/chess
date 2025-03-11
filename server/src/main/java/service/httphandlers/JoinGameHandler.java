package service.httphandlers;

import com.google.gson.Gson;
import dataaccess.*;
import service.GameService;
import service.RequestResult;
import spark.Request;
import spark.Response;

import java.util.Map;

public class JoinGameHandler {

    RequestResult.JoinGameRequest joinGameRequest;
    RequestResult.JoinGameResult joinGameResult;

    public Object joinGameHandler(Request request, Response response, Gson serializer,
                                  GameService gameService){
        try {
            String joinGameHandlerJson = request.body();
            String authToken = request.headers("authorization");
            joinGameRequest = serializer.fromJson(joinGameHandlerJson,
                    RequestResult.JoinGameRequest.class);
            if (joinGameRequest == null){
                response.status(500);
                return serializer.toJson(Map.of("message", "Error: Request is null"));
            }
            joinGameResult = gameService.joinGame(joinGameRequest, authToken);
        }
        catch (DataAccessException e){
            if (e instanceof InvalidInputException){
                response.status(403);
                return serializer.toJson(Map.of("message", "Error: Usernames are already taken"));
            }
            if (e instanceof InvalidAccessException){
                response.status(400);
                return serializer.toJson(Map.of("message", "Error: invalid playerColor requested"));
            }
            response.status(401);
            return serializer.toJson(Map.of("message", "Error: Invalid username"));
        }
        response.status(200);
        return serializer.toJson(joinGameResult);
    }

}
