package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class AuthDataDAO implements AuthDataAccessInterface{

    private final Map<String, AuthData> authData = new HashMap<>();

    public Map<String, AuthData> getAllAuthData(){
        return authData;
    }

    public void createAuth(AuthData auth){
        if (!authData.containsKey(auth.authToken())) {
            authData.put(auth.authToken(), auth);
        }
    }

    public AuthData getAuth(String authToken){
        return authData.get(authToken);
    }

    public void updateAuth(AuthData auth){
        if (authData.containsKey(auth.authToken())){
            authData.put(auth.authToken(), auth);
        }
    }

    public void deleteAuth(String authToken){
        authData.remove(authToken);
    }

}
