package dataaccess;

import model.UserData;

import java.util.ArrayList;

public class UserDataDAO implements UserDataAccessInterface{

    private final ArrayList<UserData> userData = new ArrayList<>();

    public ArrayList<UserData> getAllUserData(){
        return userData;
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

    public void updateUser(UserData user){
        for (int i = 0; i < userData.size(); i++) {
            if (userData.get(i).username().equals(user.username())) {
                userData.set(i, user);
                return;
            }
        }
    }

    public void deleteUser(String username){
        userData.removeIf(user -> user.username().equals(username));
    }
}
