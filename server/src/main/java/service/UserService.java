package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import service.RequestResult.*;
import dataaccess.InvalidInputException;
import dataaccess.InvalidUsernameException;

import java.util.UUID;

public class UserService {

    private final AuthDataAccess authDataAccess;
    private final UserDataAccess userDataAccess;

    public UserService(AuthDataAccess authDataAccess, UserDataAccess userDataAccess){
        this.authDataAccess = authDataAccess;
        this.userDataAccess = userDataAccess;
    }

    public RegisterResult registerUser(RegisterRequest r) throws DataAccessException {
        UserData userdata = new UserData(r.username(), r.password(), r.email());
        if (r.username() == null || r.password() == null || r.email() == null){
            throw new InvalidInputException("Username, password, or email must not be null");
        }
        if (userDataAccess.getUser(r.username()) != null){
            throw new InvalidUsernameException("Username is already taken");
        }
        userDataAccess.insertUser(userdata);
        String authToken = UUID.randomUUID().toString();
        authDataAccess.createAuth(new AuthData(authToken, r.username()));
        return new RegisterResult(r.username(), authToken);
    }

    public LoginResult login(LoginRequest l) throws DataAccessException {
        UserData user = userDataAccess.getUser(l.username());
        if (user == null){
            throw new InvalidUsernameException("Username is not found or incorrect");
        }

        boolean isHashedPassword = user.password().length() == 60;
        if (isHashedPassword){
            if (!BCrypt.checkpw(l.password(), user.password())){
                throw new InvalidInputException("Password does not match");
            }
        }
        else{
            if (!l.password().equals(user.password())){
                throw new InvalidInputException("Password does not match");
            }
        }

        String authToken = UUID.randomUUID().toString();
        authDataAccess.createAuth(new AuthData(authToken, l.username()));
        return new LoginResult(l.username(), authToken);
    }

    public LogoutResult logout(String authToken) throws DataAccessException {
        AuthData authData = authDataAccess.getAuth(authToken);
        if (authData == null || !authToken.equals(authData.authToken())){
            throw new InvalidInputException("AuthToken is invalid");
        }
        authDataAccess.deleteAuth(authToken);
        return null;
    }

}
