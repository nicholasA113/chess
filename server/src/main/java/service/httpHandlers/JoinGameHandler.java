package service.httpHandlers;

import com.google.gson.Gson;
import service.GameService;
import service.RequestResult;
import spark.Request;
import spark.Response;

public class JoinGameHandler {

    Gson serializer = new Gson();

    public Object joinGameHandler(Request request, Response response){
        String joinGameHandlerJson = request.body();
        RequestResult.JoinGameRequest joinGameRequest = serializer.fromJson(joinGameHandlerJson,
                RequestResult.JoinGameRequest.class);
        GameService gameService = new GameService();
        RequestResult.JoinGameResult joinGameResult = gameService.joinGame(joinGameRequest);
        response.status(200);
        return serializer.toJson(joinGameResult);
    }

}
