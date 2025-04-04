package server;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {

    private static Gson gson = new Gson();
    private static ConcurrentHashMap<String, Session> connections = new ConcurrentHashMap<>();
    private static Map<Session, Integer> sessionGameID = new HashMap<>();

    private static String notificationText;


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        switch(command.getCommandType()){
            case CONNECT -> connect(session, command);
            //case MAKE_MOVE -> makeMove(session, command);
            case LEAVE -> leave(session, command);
            //case RESIGN -> resign(session, command);
        }
    }

    public static void connect(Session session, UserGameCommand command) {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();
        ChessGame game = null;
        connections.put(authToken, session);
        sessionGameID.put(session, gameID);
        List<GameData> games = command.getGames();
        for (GameData chessGame : games){
            if (chessGame.gameID() == gameID){
                game = chessGame.game();
            }
        }
        String playerColor = command.getPlayerColor();
        if (!command.observer()){
            System.out.println("User " + command.getUsername() + " connected to game " + gameID +
                    " as " + playerColor);
        }
        else if (command.observer()){
            System.out.println("User " + command.getUsername() + " connected to game " + gameID
            + " as an observer");
        }
        LoadGameMessage loadGameMessage = new LoadGameMessage(game);
        try {
            session.getRemote().sendString(gson.toJson(loadGameMessage));
        } catch (IOException e) {
            ErrorMessage errorMessage = new ErrorMessage("Failed to send notification: " + e.getMessage());
            try {
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } catch (IOException ex) {
                System.err.println("Failed to send error message: " + ex.getMessage());
            }
        }
        if (!command.observer()){
            notificationText = command.getUsername() + " has joined the game as player color " + playerColor;
        }
        else if (command.observer()){
            notificationText = command.getUsername() + " has joined as an observer";
        }
        Notification notification = new Notification(notificationText);
        sendNotification(authToken, notification, gameID);
    }

    public static void leave(Session session, UserGameCommand command){
        if (!command.observer()){
            String authToken = command.getAuthToken();
            int gameID = command.getGameID();
            connections.remove(authToken, session);
            sessionGameID.remove(session, gameID);
            notificationText = command.getUsername() + " has left the game.";
            Notification notification = new Notification(notificationText);
            sendNotification(authToken, notification, gameID);
        }
    }

    public static void sendNotification(String authToken, Notification notification, Integer gameID){
        connections.forEach((token, session) -> {
            Integer sessionID = sessionGameID.get(session);
            if (!token.equals(authToken) && sessionID.equals(gameID)) {
                try {
                    session.getRemote().sendString(gson.toJson(notification));
                } catch (IOException e) {
                    System.err.println("Failed to send error message: " + e.getMessage());
                }
            }
        });
    }
}
