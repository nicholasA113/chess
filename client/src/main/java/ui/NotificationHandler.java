package ui;

import websocket.messages.*;

public interface NotificationHandler {
    public void handleNotification(Notification notification);
}
