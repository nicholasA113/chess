package service.httpHandlers;

import com.google.gson.Gson;
import service.UserService;
import spark.Request;
import spark.Response;
import service.RequestResult;

public class LoginHandler {

    Gson serializer = new Gson();

    public Object loginHandler(Request request, Response response){
        String loginRequestJson = request.body();
        RequestResult.LoginRequest loginRequest = serializer.fromJson(loginRequestJson, RequestResult.LoginRequest.class);
        UserService userService = new UserService();
        RequestResult.LoginResult loginResult = userService.login(loginRequest);
        response.status(200);
        return serializer.toJson(loginResult);
    }

}
