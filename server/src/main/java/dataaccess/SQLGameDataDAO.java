package dataaccess;

import com.google.gson.Gson;
import model.GameData;
import chess.ChessGame;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

public class SQLGameDataDAO implements GameDataAccessInterface{

    Gson serializer = new Gson();

    public void createGame(String gameName) throws DataAccessException {
        ArrayList<GameData> existingGameData = getAllGames();
        for (GameData game : existingGameData){
            if (game.gameName().equals(gameName)){
                throw new DataAccessException("gameName already exists in database");
            }
        }

        String insertStatement = "INSERT INTO gameData (whiteusername, " +
                "blackusername, gameName, chessGame) VALUES (?, ?, ?, ?)";
        String chessGameJSON = serializer.toJson(new ChessGame());
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement(insertStatement)){
                preparedStatement.setNull(1, Types.VARCHAR);
                preparedStatement.setNull(2, Types.VARCHAR);
                preparedStatement.setString(3, gameName);
                preparedStatement.setString(4, chessGameJSON);

                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e){
            throw new DataAccessException("Error creating game: " + e.getMessage());
        }
    }

    public GameData getGame(int gameID) throws DataAccessException{
        String getStatement = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame" +
                " FROM gameData WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement(getStatement)){
                preparedStatement.setInt(1, gameID);
                try (var result = preparedStatement.executeQuery()){
                    if (result.next()){
                        int id = result.getInt("gameID");
                        String whiteUsername = result.getString("whiteUsername");
                        String blackUsername = result.getString("blackUsername");
                        String gameName = result.getString("gameName");
                        String game = result.getString("chessGame");
                        ChessGame chessGame = serializer.fromJson(game, ChessGame.class);

                        return new GameData(id, whiteUsername, blackUsername,
                                gameName, chessGame);
                    }
                    else{
                        throw new DataAccessException("Game is not found in the database");
                    }
                }
            }
        }
        catch (SQLException e){
            throw new DataAccessException("Error getting game: " + e.getMessage());
        }
    }

    public void updateGame(GameData game) throws DataAccessException{
        String updateStatement = "UPDATE gameData SET whiteUsername = ?, blackUsername = ?, " +
                "gameName = ?, chessGame = ? WHERE gameID = ?";
        String chessGameJSON = serializer.toJson(game.game());
        try (var conn = DatabaseManager.getConnection()){
            var preparedStatement = conn.prepareStatement(updateStatement);
            preparedStatement.setString(1, game.whiteUsername());
            preparedStatement.setString(2, game.blackUsername());
            preparedStatement.setString(3, game.gameName());
            preparedStatement.setString(4, chessGameJSON);
            preparedStatement.setInt(5, game.gameID());

            int result = preparedStatement.executeUpdate();

            if (result == 0){
                throw new DataAccessException("No game was found with given gameID");
            }
        }
        catch (SQLException e){
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }

    public int getID(String gameName) throws DataAccessException{
        String getGameIDStatement = "SELECT gameID FROM gameData WHERE gameName = ?";
        try (var conn = DatabaseManager.getConnection()){
            var preparedStatement = conn.prepareStatement(getGameIDStatement);
            preparedStatement.setString(1, gameName);
            try(var result = preparedStatement.executeQuery()){
                if (result.next()){
                    return result.getInt("gameID");
                }
                else{
                    throw new DataAccessException("ID not found in gameData");
                }
            }
        }
        catch (SQLException e){
            throw new DataAccessException("Error getting gameID: " + e.getMessage());
        }
    }

    public ArrayList<GameData> getAllGames() throws DataAccessException{
        String getAllStatement = "SELECT * FROM gameData";
        ArrayList<GameData> gameDataList = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()){
            var preparedStatement = conn.prepareStatement(getAllStatement);
            var result = preparedStatement.executeQuery();
            while (result.next()){
                int gameID = result.getInt("gameID");
                String whiteUsername = result.getString("whiteUsername");
                String blackUsername = result.getString("blackUsername");
                String gameName = result.getString("gameName");
                String chessGame = result.getString("chessGame");
                ChessGame game = serializer.fromJson(chessGame, ChessGame.class);

                gameDataList.add(new GameData(gameID, whiteUsername,
                        blackUsername, gameName, game));
            }
        }
        catch (SQLException e){
            throw new DataAccessException("Error getting all gameData: " + e.getMessage());
        }
        return gameDataList;
    }

    public void clearAllGameData() throws DataAccessException {
        String clearStatement = "DELETE FROM gameData";
        try (var conn = DatabaseManager.getConnection()){
            var preparedStatement = conn.prepareStatement(clearStatement);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            throw new DataAccessException("Error clearing all gameData: " + e.getMessage());
        }
    }
}
