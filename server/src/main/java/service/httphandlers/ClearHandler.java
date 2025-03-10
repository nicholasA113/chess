package service.httphandlers;

import com.google.gson.Gson;
import dataaccess.*;
import service.ClearService;
import service.RequestResult;
import spark.Request;
import spark.Response;

import java.util.Map;

public class ClearHandler {

    RequestResult.ClearDataRequest clearDataRequest;
    RequestResult.ClearDataResult clearDataResult;

    public Object clearHandler(Request request, Response response, Gson serializer,
                               ClearService clearService){

        try {
            String clearHandlerJson = request.body();
            clearDataRequest = serializer.fromJson(clearHandlerJson,
                    RequestResult.ClearDataRequest.class);
            clearDataResult = clearService.clearData(clearDataRequest);
        }
        catch (DataAccessException e){
            if (e instanceof InvalidAccessException){
                response.status(500);
                return serializer.toJson(Map.of("message", "Error: not all data was cleared"));
            }
        }
        response.status(200);
        return serializer.toJson(clearDataResult);
    }

}
