package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import service.RequestResult.*;
import dataaccess.InvalidUsernameException;

import java.util.ArrayList;

public class GameService {

    public CreateGameResult createGame(CreateGameRequest c, String authToken,
                                       AuthDataDAO authDataDAO, GameDataDAO gameDataDAO){
        if (authDataDAO.getAuth(authToken) == null || c.gameName() == null){
            throw new InvalidInputException("AuthToken and/or Game Name is invalid or empty");
        }
        gameDataDAO.createGame(c.gameName());
        int id = gameDataDAO.getGameID();
        GameData createdGame = gameDataDAO.getGame(id);
        return new CreateGameResult(createdGame.gameID());
    }

    public JoinGameResult joinGame(JoinGameRequest j, String authToken, AuthDataDAO authDataDAO,
                                   GameDataDAO gameDataDAO){
        AuthData authData = authDataDAO.getAuth(authToken);
        GameData gameData = gameDataDAO.getGame(j.gameID());
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
        if (authDataDAO.getAuth(authToken) == null || !authToken.equals(authData.authToken())){
            throw new InvalidUsernameException("Username in invalid");
        }
        GameData game = gameDataDAO.getGame(j.gameID());
        String whiteUsername = game.whiteUsername();
        String blackUsername = game.blackUsername();

        String username = authData.username();

        if (whiteUsername == null && j.playerColor().equals("WHITE")){
            GameData updatedGame = new GameData(game.gameID(),
                    username,
                    blackUsername,
                    game.gameName(),
                    game.game());
            gameDataDAO.updateGame(updatedGame);
            return new JoinGameResult();
        }
        else if (blackUsername == null && j.playerColor().equals("BLACK")){
            GameData updatedGame = new GameData(game.gameID(),
                    whiteUsername,
                    username,
                    game.gameName(),
                    game.game());
            gameDataDAO.updateGame(updatedGame);
            return new JoinGameResult();
        }
        else{
            throw new InvalidInputException("Both users are already taken for this game");
        }
    }

    public ListGamesResult listGames(String authToken, AuthDataDAO authDataDAO, GameDataDAO gameDataDAO){
        AuthData authData = authDataDAO.getAuth(authToken);
        if (authData == null){
            throw new InvalidUsernameException("Username/AuthToken is not valid");
        }
        ArrayList<GameData> allGames = gameDataDAO.getAllGamesUser();
        return new ListGamesResult(allGames);
    }

}
