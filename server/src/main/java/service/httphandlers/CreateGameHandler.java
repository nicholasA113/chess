package service.httphandlers;

import com.google.gson.Gson;
import dataaccess.AuthDataDAO;
import dataaccess.GameDataDAO;
import service.GameService;
import service.RequestResult;
import spark.Request;
import spark.Response;

public class CreateGameHandler {

    RequestResult.CreateGameRequest createGameRequest;
    RequestResult.CreateGameResult createGameResult;

    public Object createGameHandler(Request request, Response response, Gson serializer,
                                    AuthDataDAO authDataDAO, GameDataDAO gameDataDAO, GameService gameService){
        String createGameRequestJson = request.body();
        String authToken = request.headers("authorization");

        createGameRequest = serializer.fromJson(
                createGameRequestJson, RequestResult.CreateGameRequest.class);

        createGameResult = gameService.createGame(
                createGameRequest, authToken, authDataDAO, gameDataDAO);
        response.status(200);
        return serializer.toJson(createGameResult);
    }

}
