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
import ui.DrawChessBoard.*;

public class ChessClient {


    private boolean loggedIn;
    private boolean loggedOut;
    private boolean registered;
    ServerFacade serverFacade;
    AuthData data;
    Integer mapIndex = 1;
    Map<Integer, GameData> gameMapIndexToID;
    DrawChessBoard drawChessBoard = new DrawChessBoard();


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
            /**case "observe" -> observeGame();**/

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
            registered = true;
            return String.format("Successfully registered as %s.", parameters[0]);
        }
        return "Not enough information given to register user";
    }


    public String login(String ... parameters) throws ResponseException {
        if (registered && parameters.length == 2){
            RequestResult.LoginRequest loginRequest = new RequestResult.LoginRequest(
                    parameters[0], parameters[1]);
            try{
                serverFacade.login(loginRequest);
            }
            catch (Exception e){
                throw new ResponseException(400, "Error logging in user - " + e.getMessage());
            }
            loggedIn = true;
            return String.format("Successfully logged in as %s.", parameters[0]);
        }
        return "You are not registered or did not give enough information to login";
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
        return "You need to login before being able to logout";
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
        return "You are not logged in or did not give the correct amount of information";
    }


    public String listGames() throws ResponseException{
        String gameListString = "";
        if (loggedIn){
            try{
                RequestResult.ListGamesResult listGamesResult = serverFacade.listGames(
                        data.authToken());
                List<GameData> games = listGamesResult.games();
                for (int i=1; i<games.size()+1; i++){
                    for (GameData game : games)
                        if (!gameMapIndexToID.containsKey(i) &&
                                !gameMapIndexToID.containsValue(game))
                            gameMapIndexToID.put(mapIndex++, game);
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
        return "You are not logged in";
    }


    public String joinGame(String ... parameters) throws ResponseException {
        if (loggedIn && parameters.length == 2){
            try{
                int gameID = 0;
                for (Map.Entry<Integer, GameData> game : gameMapIndexToID.entrySet()){
                    GameData gameData = game.getValue();
                    if (Integer.parseInt(parameters[0]) == game.getKey()){
                        gameID = gameData.gameID();
                    }
                }
                serverFacade.joinGame(data.authToken(), parameters[1].toUpperCase(), gameID);
                drawChessBoard.drawBoard();
                return "Successfully joined the game";
            }
            catch (Exception e){
                throw new ResponseException(400, "Error joining game - " + e.getMessage());
            }
        }
        return "You are not logged in or did not give enough information to join the game";
    }


    /**public String observeGame(String ... parameters) throws ResponseException{

    }**/


    public String help() {
        if (loggedOut) {
            return """
                    help: lists available commands
                    quit: exits the program
                    register <USERNAME> <PASSWORD> <EMAIL>: create an account
                    login <USERNAME> <PASSWORD>: login to play chess
                    """;
        }if (registered || loggedIn)
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
}
