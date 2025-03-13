package service;

import dataaccess.*;
import requestresultrecords.RequestResult.*;

public class ClearService {

    AuthDataAccess authDataAccess;
    UserDataAccess userDataAccess;
    GameDataAccess gameDataAccess;

    public ClearService(AuthDataAccess authDataAccess, UserDataAccess userDataAccess,
                        GameDataAccess gameDataAccess){
        this.authDataAccess = authDataAccess;
        this.userDataAccess = userDataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    public ClearDataResult clearData(ClearDataRequest c) throws DataAccessException {

        userDataAccess.clearAllUserData();
        authDataAccess.clearAllAuthData();
        gameDataAccess.clearAllGameData();

        if (!gameDataAccess.getAllGames().isEmpty() || !userDataAccess.getAllUserData().isEmpty() ||
                !authDataAccess.getAllAuthData().isEmpty()){
            throw new InvalidAccessException("Not all data was cleared");
        }

        return new ClearDataResult();
    }
}
