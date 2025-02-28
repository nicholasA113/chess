package service.httpHandlers;

import com.google.gson.Gson;
import dataaccess.AuthDataDAO;
import dataaccess.UserDataDAO;
import service.UserService;
import spark.Request;
import spark.Response;
import service.RequestResult;

public class LoginHandler {

    RequestResult.LoginRequest loginRequest;
    RequestResult.LoginResult loginResult;

    public Object loginHandler(Request request, Response response, Gson serializer,
                               UserDataDAO userDataDAO, AuthDataDAO authDataDAO, UserService userService){

        String loginRequestJson = request.body();
        loginRequest = serializer.fromJson(loginRequestJson,
                    RequestResult.LoginRequest.class);
        loginResult = userService.login(loginRequest, userDataDAO, authDataDAO);
        response.status(200);
        return serializer.toJson(loginResult);

    }
}
