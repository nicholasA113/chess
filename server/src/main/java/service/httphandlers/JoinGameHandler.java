package service.httphandlers;

import com.google.gson.Gson;
import dataaccess.AuthDataDAO;
import dataaccess.GameDataDAO;
import service.GameService;
import service.RequestResult;
import spark.Request;
import spark.Response;

public class JoinGameHandler {

    RequestResult.JoinGameRequest joinGameRequest;
    RequestResult.JoinGameResult joinGameResult;

    public Object joinGameHandler(Request request, Response response, Gson serializer,
                                  AuthDataDAO authDataDAO, GameDataDAO gameDataDAO, GameService gameService){
        String joinGameHandlerJson = request.body();
        String authToken = request.headers("authorization");
        joinGameRequest = serializer.fromJson(joinGameHandlerJson,
                RequestResult.JoinGameRequest.class);
        joinGameResult = gameService.joinGame(joinGameRequest, authToken, authDataDAO, gameDataDAO);
        response.status(200);
        return serializer.toJson(joinGameResult);
    }

}
