package service.httpHandlers;

import com.google.gson.Gson;
import dataaccess.AuthDataDAO;
import dataaccess.InvalidInputException;
import dataaccess.InvalidUsernameException;
import dataaccess.UserDataDAO;
import service.UserService;
import spark.Request;
import spark.Response;
import service.RequestResult;

import java.util.Map;

public class LoginHandler {

    Gson serializer = new Gson();

    UserService userService = new UserService();

    RequestResult.LoginRequest loginRequest;
    RequestResult.LoginResult loginResult;
    UserDataDAO userDataDAO = new UserDataDAO();
    AuthDataDAO authDataDAO = new AuthDataDAO();

    public Object loginHandler(Request request, Response response){
        String loginRequestJson = request.body();

        try {loginRequest = serializer.fromJson(loginRequestJson,
                    RequestResult.LoginRequest.class);
        }
        catch (Exception e){
            response.status(400);
            return serializer.toJson(Map.of("Error", "Invalid request"));
        }

        try {
            loginResult = userService.login(loginRequest, userDataDAO, authDataDAO);
            response.status(200);
            return serializer.toJson(loginResult);
        }
        catch (InvalidUsernameException e){
            response.status(401);
            return serializer.toJson(Map.of("Error", "Invalid credentials"));
        }
        catch (InvalidInputException e){
            response.status(500);
            return serializer.toJson(Map.of("Error", "Passwords do not match"));
        }
    }

}
