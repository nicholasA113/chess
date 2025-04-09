package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        MakeMoveCommand makeMoveCommand = gson.fromJson(message, MakeMoveCommand.class);
        switch(command.getCommandType()){
            case CONNECT -> connect(session, command);
            case MAKE_MOVE -> makeMove(session, makeMoveCommand);
            case LEAVE -> leave(session, command);
            case RESIGN -> resign(session, command);
        }
    }

    public static void makeMove(Session session, MakeMoveCommand command) throws DataAccessException, InvalidMoveException {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();
        ChessMove chessMove = command.ChessMove();

        SQLAuthDataDAO authDataDAO = new SQLAuthDataDAO();
        SQLGameDataDAO gameDataDAO = new SQLGameDataDAO();
        AuthData authData = authDataDAO.getAuth(command.getAuthToken());
        GameData gameData = gameDataDAO.getGame(gameID);

        String username = authData.username();
        ChessGame game = gameData.game();

        game.makeMove(chessMove);
        gameDataDAO.updateGame(gameData);
        ChessPiece chessPiece = game.getBoard().getPiece(chessMove.getStartPosition());

        LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.game());
        sendLoadGameMessage(loadGameMessage, gameID);

        notificationText = username + " has moved " +
                chessPiece.getPieceType() + " from "
                + command.ChessMove().getStartPosition() + " to "
                + command.ChessMove().getEndPosition() + ".";
        Notification notification = new Notification(notificationText);
        sendNotification(authToken, notification, gameID);
    }

    public static void connect(Session session, UserGameCommand command) throws DataAccessException {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();

        SQLAuthDataDAO authDataDAO = new SQLAuthDataDAO();
        SQLGameDataDAO gameDataDAO = new SQLGameDataDAO();

        AuthData authData = authDataDAO.getAuth(command.getAuthToken());
        GameData gameData = gameDataDAO.getGame(gameID);

        String username = authData.username();

        connections.put(authToken, session);
        sessionGameID.put(session, gameID);
        String playerColor = "";
        if (Objects.equals(gameData.whiteUsername(), username)){
            playerColor = "white";
        }
        else if (Objects.equals(gameData.blackUsername(), username)){
            playerColor = "black";
        }

        boolean observer = (!username.equals(gameData.whiteUsername()) &&
                !username.equals(gameData.blackUsername()));

        if (!observer){
            System.out.println("User " + username + " connected to game " + gameID +
                    " as " + playerColor);
        }
        else if (observer){
            System.out.println("User " + username + " connected to game " + gameID
                    + " as an observer");
        }

        LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.game());
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
        if (!observer){
            notificationText = username + " has joined the game as player color " + playerColor;
        }
        else if (observer){
            notificationText = username + " has joined as an observer";
        }
        Notification notification = new Notification(notificationText);
        sendNotification(authToken, notification, gameID);
    }

    public static void leave(Session session, UserGameCommand command) throws DataAccessException {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();

        SQLAuthDataDAO authDataDAO = new SQLAuthDataDAO();
        SQLGameDataDAO gameDataDAO = new SQLGameDataDAO();

        AuthData authData = authDataDAO.getAuth(command.getAuthToken());
        GameData gameData = gameDataDAO.getGame(gameID);
        String username = authData.username();

        boolean observer = (username != gameData.whiteUsername() &&
                username != gameData.blackUsername());

        connections.remove(authToken, session);
        sessionGameID.remove(session, gameID);
        if (observer){
            notificationText = "Observer " + username + " has left the game.";
        }
        else{
            notificationText = username + " has left the game.";
        }
        Notification notification = new Notification(notificationText);
        sendNotification(authToken, notification, gameID);
    }

    public static void resign(Session session, UserGameCommand command) throws DataAccessException {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();

        SQLAuthDataDAO authDataDAO = new SQLAuthDataDAO();
        AuthData authData = authDataDAO.getAuth(command.getAuthToken());

        String username = authData.username();

        connections.remove(authToken, session);
        sessionGameID.remove(session, gameID);

        notificationText = username + " has resigned.";
        Notification notification = new Notification(notificationText);
        sendNotification(authToken, notification, gameID);
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

    public static void sendLoadGameMessage(LoadGameMessage loadGameMessage, Integer gameID) {
        connections.forEach((token, session) -> {
            Integer sessionID = sessionGameID.get(session);
            if (sessionID != null && sessionID.equals(gameID)) {
                try {
                    session.getRemote().sendString(gson.toJson(loadGameMessage));
                } catch (IOException e) {
                    System.err.println("Failed to send LoadGameMessage: " + e.getMessage());
                }
            }
        });
    }
}
