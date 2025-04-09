package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {

    private static Gson gson = new Gson();
    private static ConcurrentHashMap<String, Session> connections = new ConcurrentHashMap<>();
    private static Map<Session, Integer> sessionGameID = new HashMap<>();

    private static String notificationText;

    private static AuthData authData;
    private static GameData gameData;

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        UserGameCommand.CommandType commandType = UserGameCommand.CommandType.valueOf(jsonObject.get("commandType").getAsString());
        switch (commandType) {
            case CONNECT -> {
                UserGameCommand connectCommand = gson.fromJson(message, UserGameCommand.class);
                connect(session, connectCommand);
            }
            case MAKE_MOVE -> {
                MakeMoveCommand makeMoveCommand = gson.fromJson(message, MakeMoveCommand.class);
                makeMove(session, makeMoveCommand);
            }
            case LEAVE -> {
                UserGameCommand leaveCommand = gson.fromJson(message, UserGameCommand.class);
                leave(session, leaveCommand);
            }
            case RESIGN -> {
                UserGameCommand resignCommand = gson.fromJson(message, UserGameCommand.class);
                resign(session, resignCommand);
            }
        }
    }


    public static void makeMove(Session session, MakeMoveCommand command) throws DataAccessException, IOException {
        //System.out.println("Received MakeMoveCommand: " + command);
        String authToken = command.getAuthToken();
        //System.out.println("AuthToken: " + command.getAuthToken());
        int gameID = command.getGameID();
        //System.out.println("GameID: " + command.getGameID());
        ChessMove chessMove = command.getChessMove();
        //System.out.println("ChessMove: " + command.getChessMove());
        if (chessMove == null) {
            ErrorMessage errorMessage = new ErrorMessage("Chess move is missing or invalid.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        SQLGameDataDAO gameDataDAO = new SQLGameDataDAO();

        try{
            getData(session, authToken, gameID);
        }
        catch (Exception e){
            ErrorMessage errorMessage = new ErrorMessage("Data is invalid.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        String username = authData.username();
        ChessGame game = gameData.game();

        ChessGame.TeamColor currentTurn = game.getTeamTurn();
        boolean isWhite = username.equals(gameData.whiteUsername());
        boolean isBlack = username.equals(gameData.blackUsername());

        if ((currentTurn == ChessGame.TeamColor.WHITE && !isWhite) ||
                (currentTurn == ChessGame.TeamColor.BLACK && !isBlack)) {
            ErrorMessage errorMessage = new ErrorMessage("It is not your turn.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        Collection<ChessMove> validMoves = game.validMoves(chessMove.getStartPosition());
        if (!validMoves.contains(chessMove)) {
            ErrorMessage errorMessage = new ErrorMessage("Invalid move.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        ChessPiece chessPiece = game.getBoard().getPiece(chessMove.getStartPosition());
        try {
            game.makeMove(chessMove);
            gameDataDAO.updateGame(gameData);
        } catch (InvalidMoveException e) {
            ErrorMessage errorMessage = new ErrorMessage("Invalid move.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.game());
        sendLoadGameMessage(loadGameMessage, gameID);

        String notificationText = username + " has moved " +
                chessPiece.getPieceType() + " from " +
                command.getChessMove().getStartPosition() + " to " +
                command.getChessMove().getEndPosition() + ".";
        Notification notification = new Notification(notificationText);
        sendNotification(authToken, notification, gameID);
    }



    public static void connect(Session session, UserGameCommand command) throws DataAccessException, IOException {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();

        getData(session, authToken, gameID);

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

    public static void leave(Session session, UserGameCommand command) throws DataAccessException, IOException {
        SQLGameDataDAO gameDataDAO = new SQLGameDataDAO();

        String authToken = command.getAuthToken();
        int gameID = command.getGameID();

        getData(session, authToken, gameID);

        String username = authData.username();
        boolean observer = !(username.equals(gameData.whiteUsername()) || username.equals(gameData.blackUsername()));
        String notificationText = "";
        if (observer){
            notificationText = "Observer " + username + " has left the game.";
        }
        else if (!observer){
            notificationText = username + " has left the game.";
        }
        connections.remove(authToken, session);
        sessionGameID.remove(session, gameID);
        GameData updatedGameData;
        if (username.equals(gameData.whiteUsername())) {
            updatedGameData = new GameData(
                    gameData.gameID(),
                    null,
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.game()
            );
        } else if (username.equals(gameData.blackUsername())) {
            updatedGameData = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    null,
                    gameData.gameName(),
                    gameData.game()
            );
        } else {
            updatedGameData = gameData;
        }
        gameDataDAO.updateGame(updatedGameData);

        Notification notification = new Notification(notificationText);
        sendNotification(authToken, notification, gameID);
    }



    public static void resign(Session session, UserGameCommand command) throws DataAccessException, IOException {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();
        SQLAuthDataDAO authDataDAO = new SQLAuthDataDAO();
        AuthData authData;
        try {
            authData = authDataDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            ErrorMessage errorMessage = new ErrorMessage("Invalid auth token or error fetching data.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }
        if (authData == null) {
            ErrorMessage errorMessage = new ErrorMessage("Invalid auth token.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

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

    public static void getData(Session session, String authToken, Integer gameID)
            throws DataAccessException, IOException {
        SQLAuthDataDAO authDataDAO = new SQLAuthDataDAO();
        SQLGameDataDAO gameDataDAO = new SQLGameDataDAO();
        try {
            authData = authDataDAO.getAuth(authToken);
            gameData = gameDataDAO.getGame(gameID);
        } catch (DataAccessException e) {
            ErrorMessage errorMessage = new ErrorMessage("Invalid auth token or error getting data.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }
        if (authData == null) {
            ErrorMessage errorMessage = new ErrorMessage("Invalid auth token.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }
        if (gameData == null) {
            ErrorMessage errorMessage = new ErrorMessage("Game not found with ID: " + gameID);
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }
    }

}
