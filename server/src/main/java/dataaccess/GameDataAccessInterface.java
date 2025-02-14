package dataaccess;

import model.GameData;

public interface GameDataAccessInterface {
    void createGame(GameData game);
    GameData getGame(int gameID);
    void updateGame(GameData game);
    void deleteGame(int game);
}
