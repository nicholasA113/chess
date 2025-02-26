package service.httpHandlers;

import com.google.gson.Gson;
import service.UserService;
import service.RequestResult;
import spark.Request;
import spark.Response;

import java.util.Map;

public class RegisterHandler {

    Gson serializer = new Gson();
    UserService userservice = new UserService();

    public Object registerHandler(Request request, Response response){
        String registerRequestJson = request.body();
        RequestResult.RegisterRequest registerRequest;
        RequestResult.RegisterResult registerResult;

        try {
            registerRequest = serializer.fromJson(registerRequestJson,
                    RequestResult.RegisterRequest.class);
        }
        catch (Exception e){
            response.status(400);
            return serializer.toJson(Map.of("Error", "Username, password, and/or email are missing."));
        }

        try {
            registerResult = userservice.registerUser(registerRequest);
        }
        catch (Exception e){
            response.status(403);
            return serializer.toJson(Map.of("Error", "Username is already taken"));
        }

        response.status(200);
        return serializer.toJson(registerResult);
    }

}
