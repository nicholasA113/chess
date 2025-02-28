package service.httphandlers;

import com.google.gson.Gson;
import dataaccess.AuthDataDAO;
import dataaccess.GameDataDAO;
import dataaccess.UserDataDAO;
import service.ClearService;
import service.RequestResult;
import spark.Request;
import spark.Response;

public class ClearHandler {

    RequestResult.ClearDataRequest clearDataRequest;
    RequestResult.ClearDataResult clearDataResult;

    public Object clearHandler(Request request, Response response, Gson serializer,
                               UserDataDAO userDataDAO, AuthDataDAO authDataDAO,
                               GameDataDAO gameDataDAO, ClearService clearService){

        String clearHandlerJson = request.body();
        clearDataRequest = serializer.fromJson(clearHandlerJson,
                RequestResult.ClearDataRequest.class);
        clearDataResult = clearService.clearData(clearDataRequest,
                userDataDAO, authDataDAO, gameDataDAO);

        response.status(200);
        return serializer.toJson(clearDataResult);
    }

}
