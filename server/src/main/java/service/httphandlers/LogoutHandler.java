package service.httphandlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requestresultrecords.RequestResult;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Map;


public class LogoutHandler {

    RequestResult.LogoutResult logoutResult;

    public Object logoutHandler(Request request, Response response, Gson serializer,
                                UserService userService){
        try {
            String authToken = request.headers("authorization");
            logoutResult = userService.logout(authToken);
        }
        catch (DataAccessException e){
            response.status(401);
            return serializer.toJson(Map.of("message", "Error: AuthToken is invalid"));
        }
        response.status(200);
        return serializer.toJson(logoutResult);
    }
}
