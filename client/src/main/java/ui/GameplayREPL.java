package ui;

import chess.*;
import com.google.gson.Gson;
import exceptions.ResponseException;
import websocket.commands.*;

import javax.management.Notification;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameplayREPL {

    StringBuilder[][] chessBoard;
    String authToken;
    int gameID;
    WebSocketFacade connection;
    Gson gson = new Gson();
    Boolean observer;
    String playerColor;
    ChessGame chessGame;

    String bgColorStart = SET_BG_COLOR_YELLOW;
    String bgColorEnd = SET_BG_COLOR_GREEN;

    String help = """
                    help: lists available commands
                    redraw: redraws the chessboard for a fresh start
                    leave: leave game
                    move: make chess move
                    resign: admit defeat
                    highlight <[Row(a-h)][Column(1-8)]>: highlight valid movies for chess piece
                    """;

    public GameplayREPL(String authToken, int gameID, StringBuilder[][] chessBoard,
                        boolean observer, String playerColor, ChessGame chessGame){
        this.chessBoard = chessBoard;
        this.authToken = authToken;
        this.gameID = gameID;
        this.observer = observer;
        this.playerColor = playerColor;
        this.chessGame = chessGame;
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
            case "resign" -> resign();
            case "highlight" -> highlight(parameters);
            case "leave" -> leave();
            default -> {
                System.out.print(help);
                System.out.print("\n");
                DrawChessBoard.printBoard(chessBoard);
                yield "";
            }
        };
    }

    public String highlight(String ... parameters){
        if (!observer && parameters.length == 1){
            int col = 0;
            int row = 0;
            if (!playerColor.equalsIgnoreCase(chessGame.getTeamTurn().toString())){
                DrawChessBoard.printBoard(chessBoard);
                return "It is not your turn. Please wait until it is your turn to highlight.";
            }
            if (playerColor.equalsIgnoreCase("white")){
                char rowChar = parameters[0].charAt(0);
                char colChar = parameters[0].charAt(1);
                col = rowChar - 'a' + 1;
                row = colChar - '0';
            }
            else if (playerColor.equalsIgnoreCase("black")){
                char rowChar = parameters[0].charAt(0);
                char colChar = parameters[0].charAt(1);
                col = ('h' - rowChar) + 1;
                row = 9 - (colChar - '0');
            }
            if (row > 8 || col > 8){
                DrawChessBoard.printBoard(chessBoard);
                return "Invalid space position. Please enter a valid space.";
            }
            ChessPosition position = new ChessPosition(row, col);
            ChessBoard board = chessGame.getBoard();
            ChessPiece chessPiece = board.getPiece(position);
            if (chessPiece == null){
                DrawChessBoard.printBoard(chessBoard);
                return "No piece at selected position. Please enter a valid position.";
            }
            if (!playerColor.equalsIgnoreCase(chessPiece.getTeamColor().toString())){
                DrawChessBoard.printBoard(chessBoard);
                return "You have selected a piece for the other team.Please select " +
                        "one of your own pieces to highlight.";
            }
            Collection<ChessMove> validMoves = chessGame.validMoves(position);
            if (validMoves.isEmpty()){
                DrawChessBoard.printBoard(chessBoard);
                return "There are no valid moves for piece at requested position.";
            }
            for (ChessMove move : validMoves){
                ChessPosition startPosition = move.getStartPosition();
                int startPositionRow = startPosition.getRow();
                int startPositionCol = startPosition.getColumn();
                StringBuilder startPositionToHighlight =
                        chessBoard[9-startPositionRow][startPositionCol];
                DrawChessBoard.highlightSpace(startPositionToHighlight, bgColorStart);

                ChessPosition endPosition = move.getEndPosition();
                int endPositionRow = endPosition.getRow();
                int endPositionCol = endPosition.getColumn();

                if (playerColor.equalsIgnoreCase("white")){
                    StringBuilder positionToHighlight = chessBoard[9-endPositionRow][endPositionCol];
                    DrawChessBoard.highlightSpace(positionToHighlight, bgColorEnd);
                }
                else if (playerColor.equalsIgnoreCase("black")){
                    StringBuilder positionToHighlight = chessBoard[9-endPositionRow][endPositionCol];
                    DrawChessBoard.highlightSpace(positionToHighlight, bgColorEnd);
                }
            }
            DrawChessBoard.printBoard(chessBoard);
            DrawChessBoard.drawChessBoard(playerColor, chessBoard);
            return "Highlighted moves for piece at requested position";
        }
        else{
            DrawChessBoard.printBoard(chessBoard);
            return "You are only an observer. You cannot interact with the game";
        }
    }

    public String redraw(){
        if (!observer){
            chessGame.getBoard().resetBoard();
            chessBoard = DrawChessBoard.drawChessBoard(playerColor, chessBoard);
            DrawChessBoard.printBoard(chessBoard);
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

    public String resign(){
        Scanner scanner = new Scanner(System.in);
        if (!observer){
            UserGameCommand resignCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN,
                    authToken, gameID);
            String command = gson.toJson(resignCommand);
            try{
                connection.sendCommand(command);
            }
            catch (Exception e){
                System.out.print(e.getMessage());
            }
            System.out.print("Are you sure to want to resign? Doing so will result in an\nautomatic" +
                    " forfeit and the other player will win the game.\n");
            var result = "";
            while (!result.equalsIgnoreCase("yes")){
                printPrompt();
                result = scanner.nextLine();
                if (result.equalsIgnoreCase("yes")){
                    DrawChessBoard.printBoard(chessBoard);
                    return "User has admitted defeat. Other player wins!";
                }
                else if (result.equalsIgnoreCase("no")){
                    DrawChessBoard.printBoard(chessBoard);
                    return "You have chosen to not forefeit. The game will continue.";
                }
                else{
                    System.out.print("Please enter YES or NO\n");
                }
            }
        }
        return "";
    }

    private void printPrompt() {
        System.out.print("\n>>> ");
    }

}
