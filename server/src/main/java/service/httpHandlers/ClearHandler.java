package service.httpHandlers;

import com.google.gson.Gson;
import service.ClearService;
import service.RequestResult;
import spark.Request;
import spark.Response;

public class ClearHandler {

    Gson serializer = new Gson();

    public Object clearHandler(Request request, Response response){
        String clearHandlerJson = request.body();
        RequestResult.ClearDataRequest clearDataRequest = serializer.fromJson(clearHandlerJson,
                RequestResult.ClearDataRequest.class);
        ClearService clearService = new ClearService();
        RequestResult.ClearDataResult clearDataResult = clearService.clearData(clearDataRequest);
        response.status(200);
        return serializer.toJson(clearDataResult);
    }

}
