package service.httpHandlers;

import com.google.gson.Gson;
import dataaccess.InvalidInputException;
import dataaccess.InvalidUsernameException;
import dataaccess.UserDataDAO;
import service.UserService;
import service.RequestResult;
import spark.Request;
import spark.Response;

import java.util.Map;

public class RegisterHandler {

    Gson serializer = new Gson();

    RequestResult.RegisterRequest registerRequest;
    RequestResult.RegisterResult registerResult;

    public Object registerHandler(Request request, Response response,
                                  UserDataDAO userDataDAO, UserService userService){
        String registerRequestJson = request.body();

        try {
            registerRequest = serializer.fromJson(registerRequestJson,
                    RequestResult.RegisterRequest.class);
        }
        catch (Exception e){
            response.status(400);
            return serializer.toJson(Map.of("Error", "Username, password, and/or email are missing."));
        }

        try {
            registerResult = userService.registerUser(registerRequest, userDataDAO);
            response.status(200);
            return serializer.toJson(registerResult);
        }
        catch (InvalidUsernameException e){
            response.status(403);
            return serializer.toJson(Map.of("Error", "Username is already taken"));
        }
        catch (InvalidInputException e){
            response.status(500);
            return serializer.toJson(Map.of("Error", "Invalid inputs"));
        }
    }

}
