package service.httpHandlers;

import com.google.gson.Gson;
import dataaccess.AuthDataDAO;
import dataaccess.GameDataDAO;
import service.GameService;
import service.RequestResult;
import spark.Request;
import spark.Response;

public class ListGamesHandler {

    RequestResult.ListGamesResult listGamesResult;

    public Object listGamesHandler(Request request, Response response, Gson serializer,
                                   AuthDataDAO authDataDAO, GameDataDAO gameDataDAO,
                                   GameService gameService){

        String authToken = request.headers("authorization");
        listGamesResult = gameService.listGames(authToken, authDataDAO, gameDataDAO);

        response.status(200);
        return serializer.toJson(listGamesResult);
    }

}
