package dataaccess;

import model.UserData;

public interface UserDataAccessInterface {
    void insertUser(UserData user);
    UserData getUser(String username);
    void updateUser(UserData user);
    void deleteUser(String username);
}
