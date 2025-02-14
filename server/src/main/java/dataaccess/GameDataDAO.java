package dataaccess;

import model.GameData;

import java.util.Map;
import java.util.HashMap;

public class GameDataDAO implements GameDataAccessInterface {

    private final Map<Integer, GameData> gameData = new HashMap<>();

    public void createGame(GameData game){
        if (!gameData.containsKey(game.gameID())){
            gameData.put(game.gameID(), game);
        }
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

}
