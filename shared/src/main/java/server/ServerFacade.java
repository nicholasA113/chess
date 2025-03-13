package server;

import java.net.*;

import requestresultrecords.RequestResult.*;

import model.*;

public class ServerFacade {

    private final String url;

    public ServerFacade(int port){
        this.url = "http://localhost:" + port;
    }

    public AuthData register(RegisterRequest request){
        var path = "/user";
        return this.makeRequest("POST", path, request, AuthData.class);
    }

    private<T> T makeRequest(String method, String path, Object request, Class<T> responseClass){
        try{
            URL fullURL = (new URI(url+path)).toURL();
            HttpURLConnection http = (HttpURLConnection) fullURL.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
        }
        catch (Exception e){

        }
    }



}
