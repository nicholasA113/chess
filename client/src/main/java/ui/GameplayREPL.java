package ui;

import exceptions.ResponseException;
import model.GameData;
import websocket.commands.UserGameCommand;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class GameplayREPL {

    Map<Integer, StringBuilder[][]> gameToChessBoard;
    Map<Integer, GameData> gameMapIndexToID;
    String authToken;
    int gameID;

    public GameplayREPL(String authToken, int gameID, Map<Integer, StringBuilder[][]> gameToChessBoard){
        this.gameToChessBoard = new HashMap<>();
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public void runGameplayRepl() throws ResponseException {
        //WebSocketFacade connection = new WebSocketFacade();
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equalsIgnoreCase("leave")){
            printPrompt();
            String line = scanner.nextLine();
            result = makeRequest(line);
            if (!result.equalsIgnoreCase("leave")) {
                System.out.println(result);
            }
        }
    }

    public String makeRequest(String input){
        var tokens = input.split(" ");
        var request = (tokens.length > 0) ? tokens[0] : "help";
        var parameters = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (request){
            //case "redraw" -> redraw(parameters);
            //case "move" -> makeMove();
            //case "resign" -> resign(parameters);
            //case "highlight" -> highlight(parameters);
            case "leave" -> "leave";
            default -> help();
        };
    }

    public void makeMove(){
        //UserGameCommand.CommandType commandType = new UserGameCommand.CommandType(MAKE_MOVE);
        UserGameCommand makeMoveCommand = new UserGameCommand(commandType, authToken, gameID);

    }

    public String help(){
        return """
                    help: lists available commands
                    redraw: redraws the chessboard for a fresh start
                    leave: leave game
                    move: make chess move
                    resign: admit defeat
                    highlight: highlight valid movies for chess piece
                    """;
    }

    private void printPrompt() {
        System.out.print("\n>>> ");
    }

}
