package service;

import dataaccess.AuthDataDAO;
import dataaccess.GameDataDAO;
import model.AuthData;
import model.GameData;
import service.RequestResult.*;

public class GameService {

    public CreateGameResult createGame(CreateGameRequest c){
        AuthDataDAO authDataDAO = new AuthDataDAO();
        AuthData authToken = authDataDAO.getAuth(c.authToken());
        if (authToken.username() == null){
            return null;
        }
        GameDataDAO gameDataDAO = new GameDataDAO();
        gameDataDAO.createGame(c.gameName());
    }

    public JoinGameResult joinGame(JoinGameRequest j){

    }

    public ListGamesResult listGames(ListGamesRequest l){

    }

}
