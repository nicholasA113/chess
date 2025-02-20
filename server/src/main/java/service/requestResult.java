package service;

import model.GameData;
import java.util.List;

public class requestResult {

    public record RegisterRequest(String username, String password, String email) {}
    public record RegisterResult(String username, String authToken) {}
    public record LoginRequest(String username, String password) {}
    public record LoginResult(String username, String authToken) {}
    public record LogoutRequest(String authToken) {}
    public record LogoutResult() {}
    public record ListGamesRequest(String authToken) {}
    public record ListGamesResult(List<GameData> GameData) {}
    public record CreateGameRequest(String authToken, String gameName) {}
    public record CreateGameResult(int gameID) {}
    public record JoinGameRequest(String authToken, String playerColor, int gameID) {}
    public record JoinGameResult() {}
    public record ClearDataRequest() {}
    public record ClearDataResult() {}

}
