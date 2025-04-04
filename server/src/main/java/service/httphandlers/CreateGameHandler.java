package service.httphandlers;

import com.google.gson.Gson;
import dataaccess.*;
import service.GameService;
import requestresultrecords.RequestResult;
import spark.Request;
import spark.Response;

import java.util.Map;

public class CreateGameHandler {

    RequestResult.CreateGameRequest createGameRequest;
    RequestResult.CreateGameResult createGameResult;

    public Object createGameHandler(Request request, Response response, Gson serializer,
                                    GameService gameService){
        try {
            String createGameRequestJson = request.body();
            String authToken = request.headers("authorization");
            createGameRequest = serializer.fromJson(
                    createGameRequestJson, RequestResult.CreateGameRequest.class);
            if (createGameRequest == null){
                response.status(500);
                return serializer.toJson(Map.of("message", "Error: Request is null"));
            }
            createGameResult = gameService.createGame(createGameRequest, authToken);
        }
        catch (DataAccessException e){
            if (e instanceof InvalidUsernameException){
                response.status(400);
                return serializer.toJson(Map.of("message", "Error: Invalid username"));
            }
            response.status(401);
            return serializer.toJson(Map.of("message", "Error: AuthToken and/or Game Name is invalid or empty"));
        }
        response.status(200);
        return serializer.toJson(createGameResult);
    }

}
