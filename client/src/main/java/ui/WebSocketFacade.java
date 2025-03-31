package ui;

import com.google.gson.Gson;
import exceptions.ResponseException;
import websocket.commands.UserGameCommand;

import javax.management.Notification;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

public class WebSocketFacade{

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(NotificationHandler notificationHandler,
                           String authToken, int gameID) throws ResponseException {
        try{
            URI url = new URI("ws://localhost:8080/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, url);
            this.notificationHandler = notificationHandler;
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    Notification notification = new Gson().fromJson(message, Notification.class);
                    notificationHandler.notify(notification);
                }
            });
            UserGameCommand connectCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            sendCommand(new Gson().toJson(connectCommand));
        }
        catch(Exception e){
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void sendCommand(String command) throws IOException {
        session.getBasicRemote().sendText(command);
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to WebSocket server.");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("WebSocket closed: " + reason);
    }

}
