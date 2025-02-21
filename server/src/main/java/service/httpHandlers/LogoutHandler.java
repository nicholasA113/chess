package service.httpHandlers;

import com.google.gson.Gson;
import service.RequestResult;
import spark.Request;
import spark.Response;


public class LogoutHandler {

    Gson serializer = new Gson();

    public Object logoutHandler(Request request, Response response){
        String logoutRequestJson = request.body();
        RequestResult.LogoutRequest logoutRequest = serializer.fromJson(logoutRequestJson, RequestResult.LogoutRequest.class);

    }

}
