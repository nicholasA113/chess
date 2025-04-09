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

@ClientEndpoint
public class WebSocketFacade{

    public Session session;
    public NotificationHandler notificationHandler;
    public Gson gson = new Gson();
    public List<GameData> games;
    public String playerColor;
    public boolean observer;

    private ChessGame chessGame;
    private StringBuilder[][] chessBoard;

    public WebSocketFacade(NotificationHandler notificationHandler,
                           String authToken, boolean observer, String username,
                           ChessGame chessGame, int gameID, List<GameData> games,
                           String playerColor) throws ResponseException {
        try{
            URI url = new URI("ws://localhost:8080/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, url);
            System.out.print("Connected to WebSocket\n");

            this.chessGame = chessGame;
            this.observer = observer;
            this.playerColor = playerColor;
            this.games = games;

            this.notificationHandler = notificationHandler;
            UserGameCommand connectCommand =
                    new UserGameCommand(UserGameCommand.CommandType.CONNECT,
                            authToken, gameID);
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
                ChessGame updatedGame =  loadGameMessage.getChessGame();
                this.chessGame = updatedGame;
                this.chessBoard = DrawChessBoard.drawChessBoard(playerColor,
                        new StringBuilder[10][10], updatedGame.getBoard());
                System.out.print('\n');
                DrawChessBoard.printBoard(this.chessBoard);
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

    public ChessGame getChessGame() {
        return this.chessGame;
    }

    public StringBuilder[][] getChessBoard(){
        return this.chessBoard;
    }

}
