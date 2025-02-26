package service.httpHandlers;

import com.google.gson.Gson;
import dataaccess.InvalidInputException;
import service.RequestResult;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Map;


public class LogoutHandler {

    Gson serializer = new Gson();

    public Object logoutHandler(Request request, Response response){
        String logoutRequestJson = request.body();
        RequestResult.LogoutResult logoutResult;

        RequestResult.LogoutRequest logoutRequest = serializer.fromJson(logoutRequestJson, RequestResult.LogoutRequest.class);
        UserService userService = new UserService();
        try {
            logoutResult = userService.logout(logoutRequest);
            response.status(200);
            return serializer.toJson(logoutResult);
        }
        catch (InvalidInputException e){
            response.status(401);
            return serializer.toJson(Map.of("Error", "AuthToken should not be null"));
        }
    }

}
