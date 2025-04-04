package ui;

import websocket.messages.*;

public interface NotificationHandler {
    void handleNotification(Notification notification);
}
