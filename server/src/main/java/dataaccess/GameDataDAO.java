package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Map;
import java.util.HashMap;

public class GameDataDAO implements GameDataAccessInterface {

    private final Map<Integer, GameData> gameData = new HashMap<>();
    private int id = 1;

    public Map<Integer, GameData> getAllGamesUser(String username){
        Map<Integer, GameData> userGames = new HashMap<>();
        for (GameData game: gameData.values()){
            if (username.equals(game.whiteUsername()) || username.equals(game.blackUsername())){
                userGames.put(game.gameID(), game);
            }
        }
        return userGames;
    }

    public Map<Integer, GameData> getAllGames(){
        return gameData;
    }

    public void createGame(String gameName){
        for (GameData game : gameData.values()){
            if (game.gameName().equals(gameName)){
                return;
            }
        }
        int id = generateGameID();
        GameData game = new GameData(id,
                null, null, gameName, new ChessGame());
        gameData.put(id, game);
    }

    public GameData getGame(int gameID){
        return gameData.get(gameID);
    }

    public void updateGame(GameData game){
        if (gameData.containsKey(game.gameID())){
            gameData.put(game.gameID(), game);
        }
    }

    public void deleteGame(int gameID){
        gameData.remove(gameID);
    }

    public int generateGameID(){
        return id++;
    }

    public int getGameID(){
        return id-1;
    }

}
