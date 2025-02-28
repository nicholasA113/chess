package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public class GameDataDAO implements GameDataAccessInterface {

    private final ArrayList<GameData> gameData = new ArrayList<>();
    private int id = 1;

    public ArrayList<GameData> getAllGamesUser(String authToken, AuthDataDAO authDataDAO){
        ArrayList<GameData> userGames = new ArrayList<>();
        for (GameData game: gameData){
            userGames.add(game);
        }
        return userGames;
    }

    public ArrayList<GameData> getAllGames(){
        return gameData;
    }

    public void clearAllGameData(){
        gameData.clear();
    }

    public void createGame(String gameName){
        for (GameData game : gameData){
            if (game.gameName().equals(gameName)){
                return;
            }
        }
        int id = generateGameID();
        GameData game = new GameData(id,
                null, null, gameName, new ChessGame());
        gameData.add(game);
    }

    public GameData getGame(int gameID){
        for (GameData game : gameData) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    public void updateGame(GameData game){
        for (int i = 0; i < gameData.size(); i++) {
            if (gameData.get(i).gameID() == game.gameID()) {
                gameData.set(i, game);
                return;
            }
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
