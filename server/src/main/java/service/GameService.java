package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import service.RequestResult.*;
import dataaccess.InvalidUsernameException;

import java.util.ArrayList;

public class GameService {

    private final AuthDataAccess authDataAccess;
    private final GameDataAccess gameDataAccess;

    public GameService(AuthDataAccess authDataAccess, GameDataAccess gameDataAccess){
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    public CreateGameResult createGame(CreateGameRequest c, String authToken) throws DataAccessException {
        if (authDataAccess.getAuth(authToken) == null || c.gameName() == null){
            throw new InvalidInputException("AuthToken and/or Game Name is invalid or empty");
        }
        gameDataAccess.createGame(c.gameName());
        int id = gameDataAccess.getID(c.gameName());
        GameData createdGame = gameDataAccess.getGame(id);
        return new CreateGameResult(createdGame.gameID());
    }

    public JoinGameResult joinGame(JoinGameRequest j, String authToken) throws DataAccessException {
        AuthData authData = authDataAccess.getAuth(authToken);
        GameData gameData = gameDataAccess.getGame(j.gameID());
        if (gameData == null || gameData.gameID() != j.gameID()){
            throw new InvalidAccessException("ID is invalid");
        }
        if (j.playerColor() == null){
            throw new InvalidAccessException("Player color is null");
        }
        if ( j.playerColor().isEmpty()){
            throw new InvalidAccessException("Input is invalid");
        }
        if (!j.playerColor().equals("WHITE") && !j.playerColor().equals("BLACK")){
            throw new InvalidAccessException("Input is invalid");
        }
        if (authDataAccess.getAuth(authToken) == null || !authToken.equals(authData.authToken())){
            throw new InvalidUsernameException("Username in invalid");
        }
        GameData game = gameDataAccess.getGame(j.gameID());
        String whiteUsername = game.whiteUsername();
        String blackUsername = game.blackUsername();

        String username = authData.username();

        if (whiteUsername == null && j.playerColor().equals("WHITE")){
            GameData updatedGame = new GameData(game.gameID(),
                    username,
                    blackUsername,
                    game.gameName(),
                    game.game());
            gameDataAccess.updateGame(updatedGame);
            return new JoinGameResult();
        }
        else if (blackUsername == null && j.playerColor().equals("BLACK")){
            GameData updatedGame = new GameData(game.gameID(),
                    whiteUsername,
                    username,
                    game.gameName(),
                    game.game());
            gameDataAccess.updateGame(updatedGame);
            return new JoinGameResult();
        }
        else{
            throw new InvalidInputException("Both users are already taken for this game");
        }
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException {
        AuthData authData = authDataAccess.getAuth(authToken);
        if (authData == null){
            throw new InvalidUsernameException("Username/AuthToken is not valid");
        }
        ArrayList<GameData> allGames = gameDataAccess.getAllGames();
        return new ListGamesResult(allGames);
    }
}
