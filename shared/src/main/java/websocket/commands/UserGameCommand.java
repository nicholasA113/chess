package websocket.commands;

import model.GameData;

import java.util.List;
import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 *
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    private final CommandType commandType;

    private final String authToken;

    private final Integer gameID;

    private final List<GameData> games;

    private final String playerColor;

    private final boolean observer;

    private final String username;

    public UserGameCommand(CommandType commandType, String authToken, boolean observer, String username,
                           Integer gameID, List<GameData> games, String playerColor) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.games = games;
        this.playerColor = playerColor;
        this.observer = observer;
        this.username = username;
    }

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Integer getGameID() {
        return gameID;
    }

    public List<GameData> getGames(){ return games; }

    public String getPlayerColor(){ return playerColor; }

    public boolean observer(){ return observer; }

    public String getUsername(){ return username; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGameCommand)) {
            return false;
        }
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getGameID());
    }
}
