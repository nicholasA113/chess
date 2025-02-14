package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class UserDataDAO implements UserDataAccessInterface{

    private final Map<String, UserData> userData = new HashMap<>();

    public void insertUser(UserData user){
        if (!userData.containsKey(user.username())) {
            userData.put(user.username(), user);
        }
    }

    public UserData getUser(String username){
        return userData.get(username);
    }

    public void updateUser(UserData user){
        if (userData.containsKey(user.username()))
            userData.put(user.username(), user);
    }

    public void deleteUser(String username){
        userData.remove(username);
    }

}
