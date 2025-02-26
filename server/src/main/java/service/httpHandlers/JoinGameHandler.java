package service.httpHandlers;

import com.google.gson.Gson;
import dataaccess.AuthDataDAO;
import dataaccess.GameDataDAO;
import service.GameService;
import service.RequestResult;
import spark.Request;
import spark.Response;

public class JoinGameHandler {

    Gson serializer = new Gson();

    public Object joinGameHandler(Request request, Response response, AuthDataDAO authDataDAO,
                                  GameDataDAO gameDataDAO, GameService gameService){
        String joinGameHandlerJson = request.body();
        RequestResult.JoinGameRequest joinGameRequest = serializer.fromJson(joinGameHandlerJson,
                RequestResult.JoinGameRequest.class);
        RequestResult.JoinGameResult joinGameResult = gameService.joinGame(joinGameRequest, authDataDAO, gameDataDAO);
        response.status(200);
        return serializer.toJson(joinGameResult);
    }

}
