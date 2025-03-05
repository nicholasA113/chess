package dataaccess;

import model.UserData;

import java.util.ArrayList;

public interface UserDataAccessInterface {
    void insertUser(UserData user);
    UserData getUser(String username);

    void clearAllUserData();
    ArrayList<UserData> getAllUserData();
}
