package service;

import dataaccess.AuthDataDAO;
import dataaccess.GameDataDAO;
import dataaccess.UserDataDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.RequestResult.*;

import java.util.Map;

public class ClearService {

    public ClearDataResult clearData(ClearDataRequest c, UserDataDAO userDataDAO,
                                     AuthDataDAO authDataDAO, GameDataDAO gameDataDAO){

        Map<Integer, GameData> allGameData = gameDataDAO.getAllGames();
        for (GameData game : allGameData.values()){
            gameDataDAO.deleteGame(game.gameID());
        }

        Map<String, UserData> allUserData = userDataDAO.getAllUserData();
        for (UserData user : allUserData.values()){
            userDataDAO.deleteUser(user.username());
        }

        Map<String, AuthData> allAuthData = authDataDAO.getAllAuthData();
        for (AuthData auth : allAuthData.values()){
            authDataDAO.deleteAuth(auth.authToken());
        }

        return new ClearDataResult();
    }
}
