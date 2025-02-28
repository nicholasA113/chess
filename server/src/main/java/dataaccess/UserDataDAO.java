package dataaccess;

import model.UserData;

import java.util.ArrayList;

public class UserDataDAO implements UserDataAccessInterface{

    private final ArrayList<UserData> userData = new ArrayList<>();

    public ArrayList<UserData> getAllUserData(){
        return userData;
    }

    public void clearAllUserData(){
        userData.clear();
    }

    public void insertUser(UserData user){
        for (UserData existingUser : userData){
            if (existingUser.username().equals(user.username())){
                return;
            }
        }
        userData.add(user);
    }

    public UserData getUser(String username){
        for (UserData user : userData) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        return null;
    }
}
