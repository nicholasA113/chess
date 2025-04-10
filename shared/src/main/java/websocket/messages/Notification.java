package websocket.messages;

public class Notification extends ServerMessage{

    String message;

    public Notification(String message){
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    @Override
    public String toString() {
        return "\nNOTIFICATION: " + message;
    }
}
