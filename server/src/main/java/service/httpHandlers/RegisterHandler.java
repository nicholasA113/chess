package service.httpHandlers;

import com.google.gson.Gson;
import service.UserService;
import service.RequestResult;
import spark.Request;
import spark.Response;

public class RegisterHandler {

    Gson serializer = new Gson();

    public Object registerHandler(Request request, Response response){
        String registerRequestJson = request.body();
        RequestResult.RegisterRequest registerRequest = serializer.fromJson(registerRequestJson,
                RequestResult.RegisterRequest.class);
        if (registerRequest.username() == null || registerRequest.password() == null ||
        registerRequest.email() == null){
            response.status(400);
            return "{Bad Request: Username, password, and/or email are empty}";
        }
        UserService userservice = new UserService();
        RequestResult.RegisterResult registerResult = userservice.registerUser(registerRequest);
        if (registerResult == null){
            response.status(403);
            return "{Username is already taken.}";
        }
        response.status(200);
        return serializer.toJson(registerResult);
    }

}
