package websocket.messages;

public class ErrorMessage extends ServerMessage{

    String error;

    public ErrorMessage(String error){
        super(ServerMessageType.ERROR);
        this.error = error;
    }
}
