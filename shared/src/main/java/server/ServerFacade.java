package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.List;
import java.util.Map;

import exceptions.*;

import com.google.gson.Gson;
import requestresultrecords.RequestResult.*;

import model.*;

public class ServerFacade {

    private final String url;

    public ServerFacade(int port){
        this.url = "http://localhost:" + port;
    }

    public AuthData register(RegisterRequest request) throws Exception {
        var path = "/user";
        return this.makeRequest("POST", path, request, AuthData.class, null);
    }

    public LoginResult login(LoginRequest request) throws Exception {
        var path = "/session";
        return this.makeRequest("POST", path, request, LoginResult.class, null);
    }

    public LogoutResult logout(String authToken) throws Exception {
        var path = "/session";
        Map<String, String> header = Map.of("authorization", authToken);
        return this.makeRequest("DELETE", path, null,
                LogoutResult.class, header);
    }

    public CreateGameResult createGame(String authToken, String gameName) throws Exception{
        var path = "/game";
        Map<String, String> header = Map.of("authorization", authToken);
        Map<String, String> body = Map.of("gameName", gameName);
        return this.makeRequest("POST", path,
                body, CreateGameResult.class, header);
    }



    public ListGamesResult listGames(String authToken) throws Exception {
        var path = "/game";
        Map<String, String> header = Map.of("authorization", authToken);
        return this.makeRequest("GET", path, null,
                ListGamesResult.class, header);
    }

    private<T> T makeRequest(String method, String path,
                             Object request, Class<T> responseClass,
                             Map<String, String> headers) throws Exception {
        try{
            URL fullURL = (new URI(url+path)).toURL();
            HttpURLConnection http = (HttpURLConnection) fullURL.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (headers != null) {
                for (var entry : headers.entrySet()) {
                    http.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            if (!method.equals("DELETE")) {
                writeBody(request, http);
            }
            http.connect();
            var status = http.getResponseCode();
            if (status / 100 != 2){
                try (InputStream responseError = http.getErrorStream()) {
                    if (responseError != null) {
                        throw ResponseException.fromJson(responseError);
                    }
                }
                throw new ResponseException(status, "other failure: " + status);
            }
            return readBody(http, responseClass);
        }
        catch (Exception e){
            throw new ResponseException(500, "Internal error: " + e.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null){
            http.addRequestProperty("Content-Type", "application/json");
            String requestData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(requestData.getBytes());
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

}
