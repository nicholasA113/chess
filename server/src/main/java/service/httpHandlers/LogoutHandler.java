package service.httpHandlers;

import com.google.gson.Gson;
import dataaccess.AuthDataDAO;
import dataaccess.InvalidInputException;
import dataaccess.UserDataDAO;
import service.RequestResult;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Map;


public class LogoutHandler {

    Gson serializer = new Gson();

    RequestResult.LogoutRequest logoutRequest;
    RequestResult.LogoutResult logoutResult;

    public Object logoutHandler(Request request, Response response,
                                AuthDataDAO authDataDAO, UserService userService){
        String logoutRequestJson = request.body();

        logoutRequest = serializer.fromJson(logoutRequestJson, RequestResult.LogoutRequest.class);

        try {
            logoutResult = userService.logout(logoutRequest, authDataDAO);
            response.status(200);
            return serializer.toJson(logoutResult);
        }
        catch (InvalidInputException e){
            response.status(401);
            return serializer.toJson(Map.of("Error", "AuthToken should not be null"));
        }
    }
}
