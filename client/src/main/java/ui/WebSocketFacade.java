package ui;

import com.google.gson.Gson;
import com.sun.nio.sctp.NotificationHandler;
import exceptions.ResponseException;

import javax.management.Notification;
import javax.websocket.ContainerProvider;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.net.URI;

public class WebSocketFacade {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(NotificationHandler notificationHandler) throws ResponseException {
        try{
            URI url = new URI("ws://localhost:8080/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, url);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    Notification notification = new Gson().fromJson(message, Notification.class);
                    //notificationHandler.notify(notification);
                }
            });
        }
        catch(Exception e){
            throw new ResponseException(500, e.getMessage());
        }
    }

}
