package service.httphandlers;

import com.google.gson.Gson;
import dataaccess.*;
import service.UserService;
import service.RequestResult;
import spark.Request;
import spark.Response;

import java.util.Map;

public class RegisterHandler {

    RequestResult.RegisterRequest registerRequest;
    RequestResult.RegisterResult registerResult;

    public Object registerHandler(Request request, Response response, Gson serializer,
                                  UserService userService){
        try {
            String registerRequestJson = request.body();
            registerRequest = serializer.fromJson(registerRequestJson,
                    RequestResult.RegisterRequest.class);
            if (registerRequest == null){
                response.status(500);
                return serializer.toJson(Map.of("message", "Error: Request is null"));
            }
            registerResult = userService.registerUser(registerRequest);
        }
        catch (DataAccessException e){
            if (e instanceof InvalidInputException){
                response.status(400);
                return serializer.toJson(Map.of("message", "Error: Invalid inputs"));
            }
            if (e instanceof InvalidUsernameException){
                response.status(403);
                return serializer.toJson(Map.of("message", "Error: Username is already taken"));
            }
        }
        response.status(200);
        return serializer.toJson(registerResult);
    }
}
