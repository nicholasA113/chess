package ui;

import exceptions.ResponseException;
import model.AuthData;
import model.GameData;
import requestresultrecords.RequestResult;
import server.ServerFacade;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChessClient {


    private boolean loggedIn;
    private boolean loggedOut;
    private boolean registered;
    ServerFacade serverFacade;
    AuthData data;
    Integer mapIndex = 1;
    Map<Integer, GameData> gameMapIndexToID;


    public ChessClient(int port){
        this.serverFacade = new ServerFacade(port);
        registered = false;
        loggedIn = false;
        this.gameMapIndexToID = new HashMap<>();
    }


    public String request(String input) throws ResponseException {
        var tokens = input.split(" ");
        var request = (tokens.length > 0) ? tokens[0] : "help";
        var parameters = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (request){
            case "register" -> register(parameters);
            case "login" -> login(parameters);
            case "logout" -> logout();
            case "create" -> createGame(parameters);
            case "list" -> listGames();
            case "join" -> joinGame(parameters);
            case "observe" -> observeGame(parameters);
            case "quit" -> "quit";
            default -> help();
        };
    }


    public String register(String ... parameters) throws ResponseException {
        if (parameters.length == 3){
            RequestResult.RegisterRequest registerRequest = new RequestResult.RegisterRequest(
                    parameters[0], parameters[1], parameters[2]);
            try{
                data = serverFacade.register(registerRequest);
            }
            catch (Exception e){
                return "Username is already taken";
            }
            loggedOut = false;
            registered = true;
            return String.format("Successfully registered as %s.", parameters[0]);
        }
        return "Not enough information given to register user. Required is a username, " +
                "password, and email.";
    }


    public String login(String ... parameters) throws ResponseException {
        if (parameters.length == 2){
            RequestResult.LoginRequest loginRequest = new RequestResult.LoginRequest(
                    parameters[0], parameters[1]);
            try{
                RequestResult.LoginResult loginResult = serverFacade.login(loginRequest);
                data = new AuthData(loginResult.authToken(), loginResult.username());
            }
            catch (Exception e){
                return "Invalid login credentials";
            }
            loggedOut = false;
            loggedIn = true;
            return String.format("Successfully logged in as %s.", parameters[0]);
        }
        else{
            return "Too many/few inputs given. Need request, username, and password";
        }
    }


    public String logout() throws ResponseException{
        if (loggedIn){
            try{
                serverFacade.logout(data.authToken());
            }
            catch (Exception e){
                throw new ResponseException(400, "Error logging out - " + e.getMessage());
            }
            loggedIn = false;
            loggedOut = true;
            return String.format("Logged out as %s", data.username());
        }
        return "You must be logged in before being able to logout.";
    }


    public String createGame(String ... parameters) throws ResponseException{
        if (loggedIn && parameters.length == 1){
            try{
                serverFacade.createGame(data.authToken(), parameters[0]);
            }
            catch (Exception e){
                throw new ResponseException(400, "Error creating game - " + e.getMessage());
            }
            return "Successfully created game";
        }
        if (!loggedIn){
            return "You are not logged in. Please login before creating a game.";
        }
        else{
            return "Too few/many arguments passed in. Need request and a game name";
        }
    }


    public String listGames() throws ResponseException{
        String gameListString = "";
        if (loggedIn){
            try{
                List<GameData> games = getUpdateGames();
                if (games.isEmpty()){
                    return "There are no games.";
                }
                for (Map.Entry<Integer, GameData> game : gameMapIndexToID.entrySet()){
                    GameData gameData = game.getValue();
                    gameListString += String.format("%d) %s [White: %s|Black: %s]\n",
                            game.getKey(), gameData.gameName(),
                            gameData.whiteUsername(), gameData.blackUsername());
                }
                return gameListString;
            }
            catch (Exception e){
                throw new ResponseException(400, "Error listing games - " + e.getMessage());
            }
        }
        return "You are not logged in. Please login before listing the games.";
    }


    public String joinGame(String ... parameters){
        if (loggedIn && parameters.length == 2){
            try{
                int gameID = 0;
                String whiteUsername = "";
                String blackUsername = "";
                getUpdateGames();
                for (Map.Entry<Integer, GameData> game : gameMapIndexToID.entrySet()){
                    GameData gameData = game.getValue();
                    if (Integer.parseInt(parameters[0]) == game.getKey()){
                        gameID = gameData.gameID();
                        whiteUsername = gameData.whiteUsername();
                        blackUsername = gameData.blackUsername();
                    }
                }
                if (parameters[1].equalsIgnoreCase("WHITE") && data.username().equals(whiteUsername)){
                    DrawChessBoard.main(new String []{});
                }
                else if (parameters[1].equalsIgnoreCase("BLACK") && data.username().equals(blackUsername)){
                    DrawFlippedChessBoard.main(new String []{});
                }
                else{
                    serverFacade.joinGame(data.authToken(), parameters[1].toUpperCase(), gameID);
                    if (parameters[1].equalsIgnoreCase("WHITE")){
                        DrawChessBoard.main(new String []{});
                    }
                    else if (parameters[1].equalsIgnoreCase("BLACK")){
                        DrawFlippedChessBoard.main(new String []{});
                    }
                }
                return "Successfully joined the game";
            }
            catch (Exception e){
                return "Player color is already taken";
            }
        }
        if (!loggedIn){
            return "You are not logged in. Please login before attempting to join game";
        }
        else {
            return "Too few/many arguments passed in. Need request, game to join, " +
                    "and desired player color";
        }
    }


    public String observeGame(String ... parameters){
        if (loggedIn && parameters.length == 1){
            DrawChessBoard.main(new String []{});
        }
        return "Observing chess game";
    }


    public String help() {
        if (loggedOut) {
            return """
                    help: lists available commands
                    quit: exits the program
                    register <USERNAME> <PASSWORD> <EMAIL>: create an account
                    login <USERNAME> <PASSWORD>: login to play chess
                    """;
        }else if (registered || loggedIn)
            return """
                    help: lists available commands
                    quit: exits the program
                    login <USERNAME> <PASSWORD>: login to play chess
                    logout: logs you out of the Chess Server
                    create <GAME NAME>: creates a game
                    list: lists all available games
                    join <GAME ID> <[WHITE|BLACK]>: join a game as a requested color
                    observe <GAME ID>: watch given game
                    """;
        else {
            return """
                    help: lists available commands
                    quit: exits the program
                    register <USERNAME> <PASSWORD> <EMAIL>: create an account
                    login <USERNAME> <PASSWORD>: login to play chess
                    """;
        }
    }

    private List<GameData> getUpdateGames() throws Exception {
        RequestResult.ListGamesResult listGamesResult = serverFacade.listGames(
                data.authToken());
        List<GameData> games = listGamesResult.games();
        for (int i=1; i<games.size()+1; i++){
            for (GameData game : games)
                if (!gameMapIndexToID.containsKey(i) &&
                        !gameMapIndexToID.containsValue(game))
                    gameMapIndexToID.put(mapIndex++, game);
        }
        return games;
    }
}
