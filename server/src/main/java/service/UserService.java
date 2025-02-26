package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import service.RequestResult.*;
import dataaccess.InvalidInputException;
import dataaccess.InvalidUsernameException;

import java.util.UUID;

public class UserService {

    UserDataDAO userDataDAO = new UserDataDAO();
    AuthDataDAO authDataDAO = new AuthDataDAO();

    public RegisterResult registerUser(RegisterRequest r){
        UserData userdata = new UserData(r.username(), r.password(), r.email());
        if (r.username() == null || r.password() == null || r.email() == null){
            throw new InvalidInputException("Username, password, or email must not be null");
        }
        if (userDataDAO.getUser(r.username()) != null){
            throw new InvalidUsernameException("Username is already taken");
        }
        userDataDAO.insertUser(userdata);
        AuthDataDAO authDataDAO = new AuthDataDAO();
        String authToken = UUID.randomUUID().toString();
        authDataDAO.createAuth(new AuthData(authToken, r.username()));
        return new RegisterResult(r.username(), authToken);
    }

    public LoginResult login(LoginRequest l){
        UserData user = userDataDAO.getUser(l.username());
        if (user == null){
            throw new InvalidUsernameException("Username is not found");
        }
        if (!user.password().equals(l.password())){
            throw new InvalidInputException("Password does not match");
        }
        String authToken = UUID.randomUUID().toString();
        authDataDAO.createAuth(new AuthData(authToken, l.username()));
        return new LoginResult(l.username(), authToken);
    }

    public LogoutResult logout(LogoutRequest l){
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
