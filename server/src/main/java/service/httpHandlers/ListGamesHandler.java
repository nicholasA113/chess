package service.httpHandlers;

import com.google.gson.Gson;
import dataaccess.AuthDataDAO;
import dataaccess.GameDataDAO;
import service.GameService;
import service.RequestResult;
import spark.Request;
import spark.Response;

public class ListGamesHandler {

    Gson serializer = new Gson();

    public Object listGamesHandler(Request request, Response response,
                                   AuthDataDAO authDataDAO, GameDataDAO gameDataDAO, GameService gameService){
        String listGamesHandlerJson = request.body();
        RequestResult.ListGamesRequest listGamesRequest = serializer.fromJson(listGamesHandlerJson,
                RequestResult.ListGamesRequest.class);
        RequestResult.ListGamesResult listGamesResult = gameService.listGames(listGamesRequest,
                authDataDAO, gameDataDAO);
        response.status(200);
        return serializer.toJson(listGamesResult);
    }

}
