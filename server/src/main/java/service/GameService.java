package service;

import dataaccess.AuthDataDAO;
import dataaccess.GameDataDAO;
import model.AuthData;
import model.GameData;
import service.RequestResult.*;

import java.util.Map;

public class GameService {

    AuthDataDAO authDataDAO = new AuthDataDAO();
    GameDataDAO gameDataDAO = new GameDataDAO();

    public CreateGameResult createGame(CreateGameRequest c){
        AuthData authToken = authDataDAO.getAuth(c.authToken());
        if (authToken.username() == null){
            return null;
        }
        gameDataDAO.createGame(c.gameName());
        GameData createdGame = gameDataDAO.getGame(gameDataDAO.getGameID()-1);
        return new CreateGameResult(createdGame.gameID());
    }

    public JoinGameResult joinGame(JoinGameRequest j){
        AuthData authToken = authDataDAO.getAuth(j.authToken());
        if (authToken.username() == null){
            return null;
        }
        GameData game = gameDataDAO.getGame(j.gameID());
        if (game.whiteUsername() == null){
            GameData updatedGame = new GameData(game.gameID(),
                    authToken.username(),
                    game.blackUsername(),
                    game.gameName(),
                    game.game());
            gameDataDAO.updateGame(updatedGame);
            return new JoinGameResult();
        }
        else if (game.blackUsername() == null){
            GameData updatedGame = new GameData(game.gameID(),
                    game.whiteUsername(),
                    authToken.username(),
                    game.gameName(),
                    game.game());
            gameDataDAO.updateGame(updatedGame);
            return new JoinGameResult();
        }
        else{
            return null;
        }
    }

    public ListGamesResult listGames(ListGamesRequest l){
        AuthData authToken = authDataDAO.getAuth(l.authToken());
        if (authToken.username() == null){
            return null;
        }
        Map<Integer, GameData> allGames = gameDataDAO.getAllGamesUser(authToken.username());
        return new ListGamesResult(allGames);
    }

}
