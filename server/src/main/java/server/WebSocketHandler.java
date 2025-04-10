package server;

import chess.*;
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

    private static Boolean resignedGame = false;

    public static Map<Integer, String> positionToLetter = new HashMap<>() {{
        put(1, "a");
        put(2, "b");
        put(3, "c");
        put(4, "d");
        put(5, "e");
        put(6, "f");
        put(7, "g");
        put(8, "h");
    }};

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
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();
        ChessMove chessMove = command.getChessMove();
        SQLAuthDataDAO authDataDAO = new SQLAuthDataDAO();
        SQLGameDataDAO gameDataDAO = new SQLGameDataDAO();
        if (chessMove == null) {
            ErrorMessage errorMessage = new ErrorMessage("Chess move is missing or invalid.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }
        try {
            authData = authDataDAO.getAuth(authToken);
            gameData = gameDataDAO.getGame(gameID);
        } catch (DataAccessException e) {
            ErrorMessage errorMessage = new ErrorMessage("Invalid auth token or error getting data.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }
        if (gameData == null) {
            ErrorMessage errorMessage = new ErrorMessage("Game not found with ID: " + gameID);
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }
        if (authData == null) {
            ErrorMessage errorMessage = new ErrorMessage("Invalid auth token.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }
        String username = authData.username();
        ChessGame game = gameData.game();
        if (resignedGame){
            ErrorMessage errorMessage = new ErrorMessage("A player has resigned. You cannot make any more moves.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }
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
        ChessGame.TeamColor opponentColor = (currentTurn == ChessGame.TeamColor.WHITE)
                ? ChessGame.TeamColor.BLACK
                : ChessGame.TeamColor.WHITE;
        if (game.isInCheckmate(opponentColor)) {
            String notificationText = username + " is in checkmate";
            Notification notification = new Notification(notificationText);
            sendNotificationAll(notification, gameID);
        }
        else if (game.isInCheck(opponentColor)) {
            String notificationText = username + " is in check.";
            Notification notification = new Notification(notificationText);
            sendNotificationAll(notification, gameID);
        }
        LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.game());
        sendLoadGameMessage(loadGameMessage, gameID);
        ChessPosition startPosition = command.getChessMove().getStartPosition();
        ChessPosition endPosition = command.getChessMove().getEndPosition();
        String startPositionCol = positionToLetter.get(startPosition.getColumn());
        String endPositionCol = positionToLetter.get(endPosition.getColumn());
        String notificationText = username + " has moved " +
                chessPiece.getPieceType() + " from " +
                startPositionCol + startPosition.getRow() + " to " +
                endPositionCol + endPosition.getRow() + ".";
        Notification notification = new Notification(notificationText);
        sendNotification(authToken, notification, gameID);
    }



    public static void connect(Session session, UserGameCommand command) throws DataAccessException, IOException {
        resignedGame = false;
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
        if (resignedGame){
            ErrorMessage errorMessage = new ErrorMessage("A player has already resigned from the game.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        String authToken = command.getAuthToken();
        int gameID = command.getGameID();

        try{
            getData(session, authToken, gameID);
        }
        catch (Exception e){
            ErrorMessage errorMessage = new ErrorMessage("Data is invalid.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        if (authData == null) {
            ErrorMessage errorMessage = new ErrorMessage("Invalid auth token.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        String username = authData.username();

        boolean observer = (!username.equals(gameData.whiteUsername()) &&
                !username.equals(gameData.blackUsername()));

        if (observer){
            ErrorMessage errorMessage = new ErrorMessage("Observer cannot resign");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        String notificationText = username + " has resigned.";
        Notification notification = new Notification(notificationText);

        sendNotificationAll(notification, gameID);

        connections.remove(authToken, session);
        sessionGameID.remove(session, gameID);

        resignedGame = true;
    }


    public static void sendNotification(String authToken, Notification notification, Integer gameID){
        connections.forEach((token, session) -> {
            Integer sessionID = sessionGameID.get(session);
            if (!token.equals(authToken) && sessionID.equals(gameID)) {
                try {
                    session.getRemote().sendString(gson.toJson(notification));
                } catch (IOException e) {
                    System.err.println("Failed to send notification: " + e.getMessage());
                }
            }
        });
    }

    public static void sendNotificationAll(Notification notification, Integer gameID){
        connections.forEach((token, session) -> {
            Integer sessionID = sessionGameID.get(session);
            if (sessionID != null && sessionID.equals(gameID)) {
                try {
                    session.getRemote().sendString(gson.toJson(notification));
                } catch (IOException e) {
                    System.err.println("Failed to send notification: " + e.getMessage());
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
