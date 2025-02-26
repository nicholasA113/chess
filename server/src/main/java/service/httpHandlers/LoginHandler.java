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

    RequestResult.LoginRequest loginRequest;
    RequestResult.LoginResult loginResult;

    public Object loginHandler(Request request, Response response,
                               UserDataDAO userDataDAO, AuthDataDAO authDataDAO, UserService userService){
        String loginRequestJson = request.body();

        loginRequest = serializer.fromJson(loginRequestJson,
                    RequestResult.LoginRequest.class);


        /** Move Try/Catch blocks to Server **/
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
            response.status(401);
            return serializer.toJson(Map.of("Error", "Passwords do not match"));
        }
    }

}
