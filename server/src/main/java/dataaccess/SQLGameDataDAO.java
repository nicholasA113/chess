package dataaccess;

import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLGameDataDAO implements GameDataAccessInterface{

    private int id = 1;

    public void createGame(String gameName) throws DataAccessException {
        String insertStatement = "INSERT INTO gameData (id, whiteusername, " +
                "blackusername, gameName, chessGame) VALUES (?, ?, ?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement(insertStatement)){
                preparedStatement.setInt(1, generateGameID());
                preparedStatement.setString(2, null);
                preparedStatement.setString(3, null);
                preparedStatement.setString(4, gameName);
                preparedStatement.setString(5, )
            }
        }
        catch (SQLException e){
            throw new DataAccessException("Error creating game: " + e.getMessage());
        }
    }

    public GameData getGame(int gameID) {
        return null;
    }

    public void updateGame(GameData game) {

    }

    public ArrayList<GameData> getAllGamesUser() {
        return null;
    }

    public ArrayList<GameData> getAllGames() {
        return null;
    }

    public void clearAllGameData() {

    }

    public int generateGameID() {
        return id++;
    }

    public int getGameID() {
        return 0;
    }
}
