package ui;

import com.google.gson.Gson;
import exceptions.ResponseException;
import websocket.commands.*;

import javax.management.Notification;
import java.util.Arrays;
import java.util.Scanner;

public class GameplayREPL {

    StringBuilder[][] chessBoard;
    String authToken;
    int gameID;
    WebSocketFacade connection;
    Gson gson = new Gson();
    Boolean observer;
    String playerColor;
    String help = """
                    help: lists available commands
                    redraw: redraws the chessboard for a fresh start
                    leave: leave game
                    move: make chess move
                    resign: admit defeat
                    highlight: highlight valid movies for chess piece
                    """;

    public GameplayREPL(String authToken, int gameID, StringBuilder[][] chessBoard,
                        boolean observer, String playerColor){
        this.chessBoard = chessBoard;
        this.authToken = authToken;
        this.gameID = gameID;
        this.observer = observer;
        this.playerColor = playerColor;
    }

    public void runGameplayRepl() throws ResponseException {
        NotificationHandler handler = new NotificationHandler() {
            @Override
            public void notify(Notification notification) {
                System.out.println("Notification received: " + notification);
            }
        };
        connection = new WebSocketFacade(handler, authToken, gameID);
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equalsIgnoreCase("leave")) {
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
            case "redraw" -> redraw();
            //case "move" -> makeMove(parameters);
            //case "resign" -> resign(parameters);
            case "highlight" -> highlight();
            case "leave" -> leave();
            default -> {
                System.out.print(help);
                System.out.print("\n");
                StringBuilder[][] reprintedChessboard = DrawChessBoard.drawChessBoard(playerColor, chessBoard);
                DrawChessBoard.printBoard(reprintedChessboard);
                yield "";
            }
        };
    }

    public String highlight(){
        if (!observer){
            chessBoard = DrawChessBoard.drawChessBoard(playerColor, chessBoard);
        }
        return "Highlighted moves for piece at requested position";
    }

    public String redraw(){
        if (!observer){
            chessBoard = DrawChessBoard.drawChessBoard(playerColor, chessBoard);
            return "Redrew the chessboard";
        }
        else{
            return "You are only an observer. You cannot interfere with gameplay.";
        }
    }

    public String leave(){
        UserGameCommand leaveCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE,
                authToken, gameID);
        String command = gson.toJson(leaveCommand);
        try{
            connection.sendCommand(command);
        }
        catch (Exception e){
            System.out.print(e.getMessage());
        }
        System.out.print("User has left the game");
        return "leave";
    }

    private void printPrompt() {
        System.out.print("\n>>> ");
    }

}
