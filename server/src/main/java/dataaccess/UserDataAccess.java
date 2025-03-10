package dataaccess;

import model.UserData;

import java.util.ArrayList;

public interface UserDataAccess {
    void insertUser(UserData user) throws DataAccessException;
    UserData getUser(String username)throws DataAccessException;

    void clearAllUserData()throws DataAccessException;
    ArrayList<UserData> getAllUserData()throws DataAccessException;
}
