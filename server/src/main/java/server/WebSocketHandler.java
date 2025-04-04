package server;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import requestresultrecords.RequestResult;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {

    private static Gson gson = new Gson();
    private static ConcurrentHashMap<String, Session> connections = new ConcurrentHashMap<>();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        switch(command.getCommandType()){
            case CONNECT -> connect(session, command);
            //case MAKE_MOVE -> makeMove(session, command);
            //case LEAVE -> leave(session, command);
            //case RESIGN -> resign(session, command);
        }
    }

    public static void connect(Session session, UserGameCommand command) throws Exception {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();
        ChessGame game = null;

        connections.put(authToken, session);
        System.out.println("User " + authToken + " connected to game " + gameID);

        List<GameData> games = command.getGames();
        for (GameData chessGame : games){
            if (chessGame.gameID() == gameID){
                game = chessGame.game();
            }
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
        String message = gson.toJson(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION));
        Notification notification = new Notification(message);
        sendNotification(notification);
    }

    public static void sendNotification(Notification notification){
        connections.forEach((_, session) -> {
            try {
                System.out.print("Sending notification...");
                session.getRemote().sendString(gson.toJson(notification));
            } catch (IOException e) {
                ErrorMessage errorMessage = new ErrorMessage("Failed to send notification: " + e.getMessage());
                try {
                    session.getRemote().sendString(new Gson().toJson(errorMessage));
                } catch (IOException ex) {
                    System.err.println("Failed to send error message: " + ex.getMessage());
                }
            }
        });
    }
}
