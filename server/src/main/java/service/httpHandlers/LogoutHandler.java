package service.httpHandlers;

import com.google.gson.Gson;
import service.RequestResult;
import service.UserService;
import spark.Request;
import spark.Response;


public class LogoutHandler {

    Gson serializer = new Gson();

    public Object logoutHandler(Request request, Response response){
        String logoutRequestJson = request.body();
        RequestResult.LogoutRequest logoutRequest = serializer.fromJson(logoutRequestJson, RequestResult.LogoutRequest.class);
        UserService userService = new UserService();
        RequestResult.LogoutResult logoutResult = userService.logout(logoutRequest);
        response.status(200);
        return serializer.toJson(logoutResult);
    }

}
