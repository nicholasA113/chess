package ui;

import chess.*;
import com.google.gson.Gson;
import exceptions.ResponseException;
import model.GameData;
import websocket.commands.UserGameCommand;

import websocket.messages.*;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static ui.EscapeSequences.*;


@ClientEndpoint
public class WebSocketFacade{

    private Session session;
    private NotificationHandler notificationHandler;
    private Gson gson = new Gson();
    private List<GameData> games;
    private String playerColor;
    private boolean observer;
    private String username;

    private ChessMove lastMove;
    private ChessPiece lastPieceMoved;

    public WebSocketFacade(NotificationHandler notificationHandler,
                           String authToken, boolean observer, String username, int gameID, List<GameData> games,
                           String playerColor) throws ResponseException {
        try{
            URI url = new URI("ws://localhost:8080/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, url);
            System.out.print("Connected to WebSocket\n");

            this.observer = observer;
            this.playerColor = playerColor;
            this.games = games;
            this.notificationHandler = notificationHandler;

            UserGameCommand connectCommand =
                    new UserGameCommand(UserGameCommand.CommandType.CONNECT,
                            authToken, observer, username, gameID, games, playerColor);
            sendCommand(gson.toJson(connectCommand));
        }
        catch(Exception e){
            throw new ResponseException(500, "Connection failed - " + e.getMessage());
        }
    }

    public void setLastMove(ChessMove move, ChessPiece piece) {
        this.lastMove = move;
        this.lastPieceMoved = piece;
    }

    @OnMessage
    public void onMessage(String message) {
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
        switch (serverMessage.getServerMessageType()){
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
                ChessGame chessGame = loadGameMessage.getChessGame();
                StringBuilder[][] chessBoard = new StringBuilder[10][10];
                chessBoard = DrawChessBoard.drawChessBoard(playerColor, chessBoard, chessGame.getBoard());
                if (lastMove != null && lastPieceMoved != null){
                    ChessPosition start = lastMove.getStartPosition();
                    ChessPosition end = lastMove.getEndPosition();
                    int rowStart = start.getRow();
                    int colStart = start.getColumn();
                    int rowEnd = end.getRow();
                    int colEnd = end.getColumn();

                    String bgColorStart = ((colStart + rowStart) % 2 == 0) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
                    String bgColorEnd = ((colEnd + rowEnd) % 2 == 0) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
                    StringBuilder startPositionToUpdate;
                    StringBuilder endPositionToUpdate;

                    if (playerColor.equalsIgnoreCase("white")) {
                        startPositionToUpdate = chessBoard[9 - rowStart][colStart];
                        endPositionToUpdate = chessBoard[9 - rowEnd][colEnd];
                        DrawChessBoard.drawChessBoard(playerColor, chessBoard, chessGame.getBoard());
                    } else {
                        startPositionToUpdate = chessBoard[rowStart][colStart];
                        endPositionToUpdate = chessBoard[rowEnd][colEnd];
                        DrawChessBoard.drawChessBoard(playerColor, chessBoard, chessGame.getBoard());
                    }
                    lastMove = null;
                    lastPieceMoved = null;
                }
                DrawChessBoard.printBoard(chessBoard);
                System.out.print("Drew chessBoard from server\n");
                printPrompt();
            }
            case NOTIFICATION -> {
                Notification notification = gson.fromJson(message, Notification.class);
                if (notificationHandler != null) {
                    notificationHandler.handleNotification(notification);
                }
            }
            case ERROR -> {
                ErrorMessage errorMessage = gson.fromJson(message, ErrorMessage.class);
                System.err.println("Error: " + errorMessage);
                printPrompt();
            }
        }
    }

    public void sendCommand(String command) throws IOException {
        if (session != null && session.isOpen()) {
            session.getBasicRemote().sendText(command);
        } else {
            throw new IOException("WebSocket connection is closed.\n");
        }
    }

    private void printPrompt() {
        System.out.print("\n>>> ");
        System.out.flush();
    }

}
