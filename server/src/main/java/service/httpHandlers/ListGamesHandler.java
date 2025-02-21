package service.httpHandlers;

import com.google.gson.Gson;
import service.GameService;
import service.RequestResult;
import spark.Request;
import spark.Response;

public class ListGamesHandler {

    Gson serializer = new Gson();

    public Object listGamesHandler(Request request, Response response){
        String listGamesHandlerJson = request.body();
        RequestResult.ListGamesRequest listGamesRequest = serializer.fromJson(listGamesHandlerJson,
                RequestResult.ListGamesRequest.class);
        GameService gameService = new GameService();
        RequestResult.ListGamesResult listGamesResult = gameService.listGames(listGamesRequest);
        response.status(200);
        return serializer.toJson(listGamesResult);
    }

}
