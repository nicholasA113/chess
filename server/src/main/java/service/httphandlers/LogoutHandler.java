package service.httphandlers;

import com.google.gson.Gson;
import dataaccess.AuthDataDAO;
import service.RequestResult;
import service.UserService;
import spark.Request;
import spark.Response;


public class LogoutHandler {

    RequestResult.LogoutResult logoutResult;

    public Object logoutHandler(Request request, Response response, Gson serializer,
                                AuthDataDAO authDataDAO, UserService userService){

        String authToken = request.headers("authorization");
        logoutResult = userService.logout(authToken, authDataDAO);
        response.status(200);
        return serializer.toJson(logoutResult);

    }
}
