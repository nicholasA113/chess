package server;

import java.net.URI;
import java.net.URL;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url){
        serverUrl = url;
    }

    public RegisterResult register(RegisterRequest request){
        var path = "/user";
        return this.makeRequest("POST", path, request, RegisterResult.class);
    }

    public LoginResult login(LoginRequest request){
        var path = "/session";
        return this.makeRequest("POST", path, request, LoginResult.class);
    }

    public LogoutResult logout(LogoutRequest request){
        var path = "/session";
        return this.makeRequest("DELETE", path, request, LogoutResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest request){
        var path = "/game";
        return this.makeRequest("POST", path, request, CreateGameResult.class);
    }

    public JoinGameResult joinGame(JoinGameRequest request){
        var path = "/game";
        return this.makeRequest("PUT", path, request, JoinGameResult.class);
    }

    public ListGamesResult listGames(ListGamesRequest request){
        var path = "/game";
        return this.makeRequest("GET", path, request, ListGamesResult.class);
    }

    public ClearResult clear(ClearRequest request){
        var path = "/db";
        return this.makeRequest("DELETE", path, request, ClearResult.class);
    }

    private<T> T makeRequest(String method, String path, Object request, Class<T> responseClass){
        try{
            URL url = (new URI(serverUrl+path)).toURL();
        }
        catch (Exception e){

        }
    }

}
