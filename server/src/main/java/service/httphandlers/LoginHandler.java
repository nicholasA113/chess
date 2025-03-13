package service.httphandlers;

import com.google.gson.Gson;
import dataaccess.*;
import service.UserService;
import spark.Request;
import spark.Response;
import requestresultrecords.RequestResult;

import java.util.Map;

public class LoginHandler {

    RequestResult.LoginRequest loginRequest;
    RequestResult.LoginResult loginResult;

    public Object loginHandler(Request request, Response response, Gson serializer,
                               UserService userService){
        try {
            String loginRequestJson = request.body();
            loginRequest = serializer.fromJson(loginRequestJson,
                    RequestResult.LoginRequest.class);
            if (loginRequest == null || userService == null){
                response.status(500);
                return serializer.toJson(Map.of("message", "Error: Request is null"));
            }
            loginResult = userService.login(loginRequest);
        }
        catch (DataAccessException e){
            if (e instanceof InvalidUsernameException){
                response.status(401);
                return serializer.toJson(Map.of("message", "Error: Invalid credentials"));
            }
            if (e instanceof InvalidInputException){
                response.status(401);
                return serializer.toJson(Map.of("message", "Error: Passwords do not match"));
            }
        }
        response.status(200);
        return serializer.toJson(loginResult);
    }
}
