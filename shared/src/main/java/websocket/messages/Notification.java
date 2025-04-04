package websocket.messages;

public class Notification extends ServerMessage{

    String notification;

    public Notification(String notification){
        super(ServerMessageType.NOTIFICATION);
        this.notification = notification;
    }

    @Override
    public String toString() {
        return "Notification: " + notification;
    }
}
