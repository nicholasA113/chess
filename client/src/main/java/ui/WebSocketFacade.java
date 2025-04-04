package ui;

import com.google.gson.Gson;
import exceptions.ResponseException;
import model.GameData;
import websocket.commands.UserGameCommand;

import javax.management.Notification;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

@ClientEndpoint
public class WebSocketFacade{

    private Session session;
    private NotificationHandler notificationHandler;
    private Gson gson = new Gson();
    List<GameData> games;

    public WebSocketFacade(NotificationHandler notificationHandler,
                           String authToken, int gameID, List<GameData> games) throws ResponseException {
        try{
            URI url = new URI("ws://localhost:8080/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, url);
            System.out.print("Connected to WebSocket\n");

            this.games = games;
            this.notificationHandler = notificationHandler;

            UserGameCommand connectCommand =
                    new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, games);
            sendCommand(gson.toJson(connectCommand));
        }
        catch(Exception e){
            throw new ResponseException(500, "Connection failed - " + e.getMessage());
        }
    }

    @OnMessage
    public void onMessage(String message) {
        Notification notification = gson.fromJson(message, Notification.class);
        if (notificationHandler != null) {
            notificationHandler.notify(notification);
        }
    }

    public void sendCommand(String command) throws IOException {
        if (session != null && session.isOpen()) {
            session.getBasicRemote().sendText(command);
        } else {
            throw new IOException("WebSocket connection is closed.");
        }
    }

}
