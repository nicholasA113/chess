package ui;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import exceptions.ResponseException;
import model.GameData;
import websocket.commands.UserGameCommand;

import websocket.messages.*;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@ClientEndpoint
public class WebSocketFacade{

    private Session session;
    private NotificationHandler notificationHandler;
    private Gson gson = new Gson();
    private List<GameData> games;
    private String playerColor;
    private boolean observer;
    private String username;

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

    @OnMessage
    public void onMessage(String message) {
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
        switch (serverMessage.getServerMessageType()){
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
                ChessGame game = loadGameMessage.getChessGame();
                ChessBoard board = game.getBoard();
                StringBuilder[][] chessBoard = new StringBuilder[10][10];
                chessBoard = DrawChessBoard.drawChessBoard(playerColor, chessBoard);
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
