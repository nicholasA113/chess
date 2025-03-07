package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import service.RequestResult.*;
import dataaccess.InvalidInputException;
import dataaccess.InvalidUsernameException;

import java.util.UUID;

public class UserService {

    public RegisterResult registerUser(RegisterRequest r, UserDataDAO userDataDAO, AuthDataDAO authDataDAO){
        UserData userdata = new UserData(r.username(), r.password(), r.email());
        if (r.username() == null || r.password() == null || r.email() == null){
            throw new InvalidInputException("Username, password, or email must not be null");
        }
        if (userDataDAO.getUser(r.username()) != null){
            throw new InvalidUsernameException("Username is already taken");
        }
        userDataDAO.insertUser(userdata);
        String authToken = UUID.randomUUID().toString();
        authDataDAO.createAuth(new AuthData(authToken, r.username()));
        return new RegisterResult(r.username(), authToken);
    }

    public LoginResult login(LoginRequest l, UserDataDAO userDataDAO, AuthDataDAO authDataDAO){
        UserData user = userDataDAO.getUser(l.username());
        if (user == null){
            throw new InvalidUsernameException("Username is not found or incorrect");
        }
        if (!user.password().equals(l.password())){
            throw new InvalidInputException("Password does not match");
        }
        String authToken = UUID.randomUUID().toString();
        authDataDAO.createAuth(new AuthData(authToken, l.username()));
        return new LoginResult(l.username(), authToken);
    }

    public LogoutResult logout(String authToken, AuthDataDAO authDataDAO){
        AuthData authData = authDataDAO.getAuth(authToken);
        if (authData == null || !authToken.equals(authData.authToken())){
            throw new InvalidInputException("AuthToken is invalid");
        }
        authDataDAO.deleteAuth(authToken);
        return null;
    }

}
