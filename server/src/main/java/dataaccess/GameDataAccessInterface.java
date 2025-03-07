package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDataAccessInterface{
    void createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;

    ArrayList<GameData> getAllGames() throws DataAccessException;
    void clearAllGameData() throws DataAccessException;
}
