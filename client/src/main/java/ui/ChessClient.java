package ui;

import exceptions.ResponseException;
import model.AuthData;
import model.GameData;
import requestresultrecords.RequestResult;
import server.ServerFacade;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ChessClient {


    private boolean loggedIn;
    private boolean registered;
    ServerFacade server;
    AuthData data;
    Integer mapIndex = 1;
    Map<Integer, Integer> gameMapIndexToID;


    public ChessClient(int port){
        this.server = new ServerFacade(port);
        registered = false;
        loggedIn = false;
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
            case "join" -> joinGame();
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
                data = server.register(registerRequest);
            }
            catch (Exception e){
                throw new ResponseException(400, "Error registering user - " + e.getMessage());
            }
            registered = true;
            return String.format("Successfully registered as %s.", parameters[0]);
        }
        throw new ResponseException(400, "Error: Not enough parameters to register user");
    }


    public String login(String ... parameters) throws ResponseException {
        if (registered && parameters.length == 2){
            RequestResult.LoginRequest loginRequest = new RequestResult.LoginRequest(
                    parameters[0], parameters[1]);
            try{
                server.login(loginRequest);
            }
            catch (Exception e){
                throw new ResponseException(400, "Error logging in user - " + e.getMessage());
            }
            loggedIn = true;
            return String.format("Successfully logged in as %s.", parameters[0]);
        }
        throw new ResponseException(400, "Error: Not enough/Too many parameters to login");
    }


    public String logout() throws ResponseException{
        if (loggedIn){
            try{
                server.logout(data.authToken());
            }
            catch (Exception e){
                throw new ResponseException(400, "Error logging out - " + e.getMessage());
            }
            loggedIn = false;
            return String.format("Logged out as %s", data.username());
        }
        throw new ResponseException(400, "Error: You need to login before logging out");
    }


    public String createGame(String ... parameters) throws ResponseException{
        if (loggedIn && parameters.length == 1){
            try{
                RequestResult.CreateGameResult createGameResult = server.createGame(data.authToken(), parameters[0]);
                gameMapIndexToID.put(mapIndex++, createGameResult.gameID());
            }
            catch (Exception e){
                throw new ResponseException(400, "Error creating game - " + e.getMessage());
            }
            return "Successfully created game";
        }
        throw new ResponseException(400, "Error: Not logged in or too many/not enough parameters given");
    }


    public String listGames() throws ResponseException{
        if (loggedIn){
            try{
                RequestResult.ListGamesResult listGamesResult = server.listGames(data.authToken());
                List<GameData> games = listGamesResult.games();
            }
            catch (Exception e){
                throw new ResponseException(400, "Error listing games - " + e.getMessage());
            }
        }
        throw new ResponseException(400, "Error: Not logged in");
    }


    public String joinGame(String ... parameters) throws ResponseException {
        if (loggedIn && parameters.length == 3){
            try{
                server.joinGame(data.authToken(), parameters[1], gameMapIndexToID.get(parameters[2]));
            }
            catch (Exception e){
                throw new ResponseException(400, "Error joining game - " + e.getMessage());
            }
        }
        throw new ResponseException(400, "Error: Not logged in");
    }


    /**public String observeGame(String ... parameters) throws ResponseException{

    }**/


    public String help(){
        if (registered){
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
        }
        return """
               help: lists available commands
               quit: exits the program
               register <USERNAME> <PASSWORD> <EMAIL>: create an account
               login <USERNAME> <PASSWORD>: login to play chess
               """;
    }
}
