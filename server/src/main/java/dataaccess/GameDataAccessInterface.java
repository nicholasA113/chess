package dataaccess;

import model.GameData;

public interface GameDataAccessInterface {
    void createGame(String gameName);
    GameData getGame(int gameID);
    void updateGame(GameData game);
    void deleteGame(int game);
}
