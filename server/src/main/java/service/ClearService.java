package service;

import dataaccess.AuthDataDAO;
import dataaccess.GameDataDAO;
import dataaccess.InvalidAccessException;
import dataaccess.UserDataDAO;
import service.RequestResult.*;

public class ClearService {

    public ClearDataResult clearData(ClearDataRequest c, UserDataDAO userDataDAO,
                                     AuthDataDAO authDataDAO, GameDataDAO gameDataDAO){

        userDataDAO.clearAllUserData();
        authDataDAO.clearAllAuthData();
        gameDataDAO.clearAllGameData();

        if (!gameDataDAO.getAllGames().isEmpty() || !userDataDAO.getAllUserData().isEmpty() ||
                !authDataDAO.getAllAuthData().isEmpty()){
            throw new InvalidAccessException("Not all data was cleared");
        }

        return new ClearDataResult();
    }
}
