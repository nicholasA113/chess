package ui;

import exceptions.ResponseException;
import model.AuthData;
import requestresultrecords.RequestResult;
import server.ServerFacade;

import java.util.Arrays;

public class ChessClient {


    private boolean registered;
    ServerFacade server;


    public ChessClient(int port){
        this.server = new ServerFacade(port);
        registered = false;
    }


    public String request(String input) throws ResponseException {
        var tokens = input.split(" ");
        var request = (tokens.length > 0) ? tokens[0] : "help";
        var parameters = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (request){
            case "register" -> register(parameters);
            case "login" -> login(parameters);
            case "quit" -> "quit";
            default -> help();
        };
    }


    public String register(String ... parameters) throws ResponseException {
        if (parameters.length == 3){
            RequestResult.RegisterRequest registerRequest = new RequestResult.RegisterRequest(
                    parameters[0], parameters[1], parameters[2]);
            try{
                server.register(registerRequest);
            }
            catch (Exception e){
                throw new ResponseException(400, "Error registering user");
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
                throw new ResponseException(400, "Error logging in user");
            }
            return String.format("Successfully logged in as %s.", parameters[0]);
        }
        throw new ResponseException(400, "Error: Not enough parameters to login");
    }


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
