package ui;

import chess.*;
import com.google.gson.Gson;
import model.GameData;
import websocket.commands.*;

import javax.management.Notification;
import java.util.*;

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
    List<GameData> games;

    String bgColorStart = SET_BG_COLOR_YELLOW;
    String bgColorEnd = SET_BG_COLOR_GREEN;

    String observerHelp = """
                    help: lists available commands
                    leave: leave game
                    highlight <[Row(a-h)][Column(1-8)]>: highlight valid movies for chess piece
                    """;
    String help = """
                    help: lists available commands
                    redraw: redraws the chessboard for a fresh start
                    leave: leave game
                    move <position to move from> <position to move to>: make chess move
                    resign: admit defeat
                    highlight <[Row(a-h)][Column(1-8)]>: highlight valid movies for chess piece
                    """;

    public GameplayREPL(String authToken, int gameID, StringBuilder[][] chessBoard,
                        boolean observer, String playerColor, ChessGame chessGame,
                        List<GameData> games){
        this.chessBoard = chessBoard;
        this.authToken = authToken;
        this.gameID = gameID;
        this.observer = observer;
        this.playerColor = playerColor;
        this.chessGame = chessGame;
        this.games = games;
    }

    public void runGameplayRepl() throws Exception {
        NotificationHandler handler = new NotificationHandler() {
            @Override
            public void notify(Notification notification) {
                System.out.println(notification);
            }
        };
        connection = new WebSocketFacade(handler, authToken, gameID, games);
        DrawChessBoard.printBoard(chessBoard);
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

    public String makeRequest(String input) throws Exception {
        var tokens = input.split(" ");
        var request = (tokens.length > 0) ? tokens[0] : "help";
        var parameters = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (request){
            case "redraw" -> redraw();
            case "move" -> makeMove(parameters);
            case "resign" -> resign();
            case "highlight" -> highlight(parameters);
            case "leave" -> leave();
            default -> {
                if (!observer){
                    System.out.print(help);
                }
                else{
                    System.out.print(observerHelp);
                }
                System.out.print("\n");
                DrawChessBoard.printBoard(chessBoard);
                yield "";
            }
        };
    }

    public String makeMove(String ... parameters) throws Exception{
        if (!observer && parameters.length == 2){
            if (!playerColor.equalsIgnoreCase(chessGame.getTeamTurn().toString())){
                DrawChessBoard.printBoard(chessBoard);
                return "It is not your turn. Please wait until it is your turn to highlight.";
            }
            int colStart = 0;
            int rowStart = 0;
            int colEnd = 0;
            int rowEnd = 0;
            char rowCharStart = parameters[0].charAt(0);
            char colCharStart = parameters[0].charAt(1);
            char rowCharEnd = parameters[1].charAt(0);
            char colCharEnd = parameters[1].charAt(1);
            colStart = rowCharStart - 'a' + 1;
            rowStart = colCharStart - '0';
            colEnd = rowCharEnd - 'a' + 1;
            rowEnd = colCharEnd - '0';
            if (rowStart > 8 || colStart > 8 || rowStart <= 0 || colStart <= 0 ||
                rowEnd > 8 || colEnd > 8 || rowEnd <= 0 || colEnd <= 0){
                DrawChessBoard.printBoard(chessBoard);
                return "Invalid space position. Please enter a valid space.";
            }
            if ((colStart + rowStart) % 2 == 0) {
                bgColorStart = SET_BG_COLOR_BLACK;
            } else {
                bgColorStart = SET_BG_COLOR_WHITE;
            }
            if ((colEnd + rowEnd) % 2 == 0) {
                bgColorEnd = SET_BG_COLOR_BLACK;
            } else {
                bgColorEnd = SET_BG_COLOR_WHITE;
            }
            ChessPosition position = new ChessPosition(rowStart, colStart);
            ChessBoard board = chessGame.getBoard();
            ChessPiece chessPiece = board.getPiece(position);
            if (chessPiece == null){
                DrawChessBoard.printBoard(chessBoard);
                return "No piece at selected position. Please enter a valid position.";
            }
            Collection<ChessMove> validMoves = chessGame.validMoves(position);
            ChessPosition endPosition = new ChessPosition(rowEnd, colEnd);
            boolean isValidMove = false;
            ChessMove chessMove = null;
            for (ChessMove move : validMoves) {
                if (move.getEndPosition().equals(endPosition)) {
                    isValidMove = true;
                    chessMove = move;
                    break;
                }
            }
            if (isValidMove) {
                if (playerColor.equalsIgnoreCase("white")){
                    ChessPosition startPosition = chessMove.getStartPosition();
                    int startPositionRow = startPosition.getRow();
                    int startPositionCol = startPosition.getColumn();
                    int endPositionRow = endPosition.getRow();
                    int endPositionCol = endPosition.getColumn();
                    StringBuilder startPositionToUpdate =
                            chessBoard[9-startPositionRow][startPositionCol];
                    StringBuilder endPositionToUpdate =
                            chessBoard[9-endPositionRow][endPositionCol];

                    DrawChessBoard.moveChessPiece(startPositionToUpdate,
                            endPositionToUpdate, bgColorStart, bgColorEnd, chessPiece,
                            SET_TEXT_COLOR_RED);
                    DrawChessBoard.printBoard(chessBoard);
                    chessGame.makeMove(chessMove);
                    return "Moved chess piece successfully";
                }

                else if (playerColor.equalsIgnoreCase("black")){
                    ChessPosition startPosition = chessMove.getStartPosition();
                    int startPositionRow = startPosition.getRow();
                    int startPositionCol = startPosition.getColumn();
                    int endPositionRow = endPosition.getRow();
                    int endPositionCol = endPosition.getColumn();
                    StringBuilder startPositionToUpdate =
                            chessBoard[startPositionRow][9-startPositionCol];
                    StringBuilder endPositionToUpdate =
                            chessBoard[endPositionRow][9-endPositionCol];

                    DrawChessBoard.moveChessPiece(startPositionToUpdate,
                            endPositionToUpdate, bgColorStart, bgColorEnd,
                            chessPiece, SET_TEXT_COLOR_BLUE);
                    chessGame.makeMove(chessMove);
                    return "Moved chess piece successfully";
                }
            } else {
                DrawChessBoard.printBoard(chessBoard);
                return "Move is not valid. Please enter a valid move.";
            }
        }
        DrawChessBoard.printBoard(chessBoard);
        return "You are an observer. You cannot make a move.";
    }

    public String highlight(String ... parameters){
        if (parameters.length == 1){
            int col = 0;
            int row = 0;
            char rowChar = parameters[0].charAt(0);
            char colChar = parameters[0].charAt(1);
            col = rowChar - 'a' + 1;
            row = colChar - '0';
            if (row > 8 || col > 8 || row <= 0 || col <= 0){
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
            Collection<ChessMove> validMoves = chessGame.validMoves(position);
            if (validMoves.isEmpty()){
                DrawChessBoard.printBoard(chessBoard);
                return "There are no valid moves for piece at requested position.";
            }
            for (ChessMove move : validMoves){
                if (playerColor.equalsIgnoreCase("white")){
                    ChessPosition startPosition = move.getStartPosition();
                    int startPositionRow = startPosition.getRow();
                    int startPositionCol = startPosition.getColumn();
                    StringBuilder startPositionToHighlight =
                            chessBoard[9-startPositionRow][startPositionCol];
                    DrawChessBoard.highlightSpace(startPositionToHighlight, bgColorStart,
                            9-startPositionRow, startPositionCol, board);

                    ChessPosition endPosition = move.getEndPosition();
                    int endPositionRow = endPosition.getRow();
                    int endPositionCol = endPosition.getColumn();

                    StringBuilder positionToHighlight = chessBoard[9-endPositionRow][endPositionCol];
                    DrawChessBoard.highlightSpace(positionToHighlight, bgColorEnd,
                            endPositionRow, 9-endPositionCol, board);
                }
                else if (playerColor.equalsIgnoreCase("black")){
                    ChessPosition startPosition = move.getStartPosition();
                    int startPositionRow = startPosition.getRow();
                    int startPositionCol = startPosition.getColumn();
                    StringBuilder startPositionToHighlight =
                            chessBoard[startPositionRow][9-startPositionCol];
                    DrawChessBoard.highlightSpace(startPositionToHighlight, bgColorStart,
                            9-startPositionRow, startPositionCol, board);

                    ChessPosition endPosition = move.getEndPosition();
                    int endPositionRow = endPosition.getRow();
                    int endPositionCol = endPosition.getColumn();

                    StringBuilder positionToHighlight = chessBoard[endPositionRow][9-endPositionCol];
                    DrawChessBoard.highlightSpace(positionToHighlight, bgColorEnd,
                            endPositionRow, 9-endPositionCol, board);
                }
            }
            DrawChessBoard.printBoard(chessBoard);
            DrawChessBoard.drawChessBoard(playerColor, chessBoard);
            return "Highlighted moves for piece at requested position";
        }
        return "Invalid input. Please try again.";
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
                authToken, gameID, games);
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
                    authToken, gameID, games);
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
