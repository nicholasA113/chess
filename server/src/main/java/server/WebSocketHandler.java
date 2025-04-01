package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;

import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {

    private static Gson gson = new Gson();
    private static ConcurrentHashMap<String, Session> connections = new ConcurrentHashMap<>();

    @OnWebSocketMessage
    public void onMessage(Session session, String message){
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        switch(command.getCommandType()){
            case CONNECT:
                connect(session, command);
        }
    }

    public static void connect(Session session, UserGameCommand command){
        String authToken = command.getAuthToken();
        connections.put(authToken, session);
        System.out.println("User connected: " + authToken);
    }

}
