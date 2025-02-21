package service.httpHandlers;

import com.google.gson.Gson;
import service.GameService;
import service.RequestResult;
import spark.Request;
import spark.Response;

public class CreateGameHandler {

    Gson serializer = new Gson();

    public Object createGameHandler(Request request, Response response){
        String createGameRequestJson = request.body();
        RequestResult.CreateGameRequest createGameRequest = serializer.fromJson(
                createGameRequestJson, RequestResult.CreateGameRequest.class);
        GameService gameService = new GameService();
        RequestResult.CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        response.status(200);
        return serializer.toJson(createGameResult);
    }

}
