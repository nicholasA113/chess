package service.httphandlers;

import com.google.gson.Gson;
import dataaccess.AuthDataDAO;
import dataaccess.UserDataDAO;
import service.UserService;
import service.RequestResult;
import spark.Request;
import spark.Response;

public class RegisterHandler {

    RequestResult.RegisterRequest registerRequest;
    RequestResult.RegisterResult registerResult;

    public Object registerHandler(Request request, Response response, Gson serializer,
                                  UserDataDAO userDataDAO, AuthDataDAO authDataDAO, UserService userService){

        String registerRequestJson = request.body();
        registerRequest = serializer.fromJson(registerRequestJson,
                RequestResult.RegisterRequest.class);
        registerResult = userService.registerUser(registerRequest, userDataDAO, authDataDAO);
        response.status(200);
        return serializer.toJson(registerResult);

    }
}
