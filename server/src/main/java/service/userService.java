package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import service.RequestResultRecords.*;

import java.util.UUID;

public class userService {

    public RegisterResult registerUser(RegisterRequest r){
        UserDataDAO userDataDAO = new UserDataDAO();
        UserData userdata = new UserData(r.username(), r.password(), r.email());
        UserData user = userDataDAO.getUser(r.username());
        if (user != null){
            return null;
        }
        userDataDAO.insertUser(userdata);
        AuthDataDAO authDataDAO = new AuthDataDAO();
        String authToken = UUID.randomUUID().toString();
        authDataDAO.createAuth(new AuthData(authToken, r.username()));
        return new RegisterResult(r.username(), authToken);
    }

    public LoginResult login(LoginRequest l){
        UserDataDAO userDataDAO = new UserDataDAO();
        UserData user = userDataDAO.getUser(l.username());
        if (user == null){
            return null;
        }
        AuthDataDAO authDataDAO = new AuthDataDAO();
        String authToken = UUID.randomUUID().toString();
        authDataDAO.createAuth(new AuthData(authToken, l.username()));
        return new LoginResult(l.username(), authToken);
    }

    public LogoutResult logout(LogoutRequest l){
        AuthDataDAO authDataDAO = new AuthDataDAO();
        AuthData authToken = authDataDAO.getAuth(l.authToken());
        if (authToken != null){
            authDataDAO.deleteAuth(l.authToken());
            return new LogoutResult();
        }
        else{
            return null;
        }
    }

}
