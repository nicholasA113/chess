package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDataAccessInterface {
    void createGame(String gameName);
    GameData getGame(int gameID);
    void updateGame(GameData game);
    ArrayList<GameData> getAllGamesUser();
    ArrayList<GameData> getAllGames();
    void clearAllGameData();
    int generateGameID();
    int getGameID();
}
