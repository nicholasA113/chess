package service;

import dataaccess.InvalidInputException;
import dataaccess.InvalidUsernameException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServiceClassTests {

    RequestResult.RegisterRequest registerRequest = new RequestResult.RegisterRequest("nicholasUsername", "123456",
            "123@bmail.com");
    RequestResult.LoginRequest loginRequest = new RequestResult.LoginRequest("nicholasUsername",
            "123456");

    UserService userServiceInstance = new UserService();

    @Test
    @DisplayName("Register Endpoint Valid Inputs")
    public void registerEndpointValid() {
        /** When **/
        RequestResult.RegisterResult actualRegisterResult = userServiceInstance.registerUser(registerRequest);

        /** Then **/
        Assertions.assertNotNull(actualRegisterResult, "The register result should not be null");
        Assertions.assertEquals("nicholasUsername", actualRegisterResult.username(), "Usernames should be the same");
        Assertions.assertNotNull(actualRegisterResult.authToken(), "AuthToken should not be null");
    }

    @Test
    @DisplayName("Register Endpoint Invalid Inputs")
    public void registerEndpointInvalid(){
        /**  Given **/
        var registerRequestNull = new RequestResult.RegisterRequest(
                null, null, "123456@gmail.com");

        /** When **/
        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                userServiceInstance.registerUser(registerRequestNull));

        /** Then **/
        Assertions.assertEquals("Username, password, or email must not be null", exception.getMessage(),
                "Exception message should indicate one of the one of the fields is missing");
    }

    @Test
    @DisplayName("User already taken")
    public void registerUserTaken(){
        /** Given**/
        var registerRequestUserTaken = new RequestResult.RegisterRequest(
                "nicholasUsername", "Password123", "nicholas@gmail.com");

        /** When **/
        RequestResult.RegisterResult registerResult1 = userServiceInstance.registerUser(registerRequest);
        InvalidUsernameException exception = assertThrows(InvalidUsernameException.class, () ->
                userServiceInstance.registerUser(registerRequestUserTaken));

        /** Then **/
        Assertions.assertNotNull(registerResult1, "First input should not be null");
        Assertions.assertEquals("Username is already taken", exception.getMessage(),
                "Error message should indicate that the username is already taken");
    }

    @Test
    @DisplayName("Login with valid credentials")
    public void loginValidInputs(){

        /** When **/
        RequestResult.RegisterResult registerResult = userServiceInstance.registerUser(registerRequest);
        RequestResult.LoginResult loginResult = userServiceInstance.login(loginRequest);

        /** Then **/
        Assertions.assertNotNull(registerResult, "Register Result should not be null");
        Assertions.assertNotNull(loginResult, "Login Result should not be null");
        Assertions.assertEquals("nicholasUsername", loginResult.username(), "Usernames should match");
        Assertions.assertNotNull(loginResult.authToken(), "Auth Token should not be null");
    }

    @Test
    @DisplayName("Login with invalid password")
    public void loginInvalidPassword(){
        /** Given **/
        var loginRequestInvalidPassword = new RequestResult.LoginRequest("nicholasUsername",
                "123");

        /** When **/
        RequestResult.RegisterResult registerResult = userServiceInstance.registerUser(registerRequest);
        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                userServiceInstance.login(loginRequestInvalidPassword));

        /** Then **/
        Assertions.assertNotNull(registerResult, "Register Result should not be null");
        Assertions.assertEquals("Password does not match", exception.getMessage(),
                "Error message should indicate that passwords do not match");
    }

    @Test
    @DisplayName("Login with invalid username")
    public void loginInvalidUsername(){
        /** Given **/
        var loginRequestInvalidUsername = new RequestResult.LoginRequest("nicholas",
                "123456");

        /** When **/
        RequestResult.RegisterResult registerResult = userServiceInstance.registerUser(registerRequest);
        InvalidUsernameException exception = assertThrows(InvalidUsernameException.class, () ->
                userServiceInstance.login(loginRequestInvalidUsername));

        /** Then **/
        Assertions.assertNotNull(registerResult, "Register Result should not be null");
        Assertions.assertEquals("Username is not found or incorrect", exception.getMessage(),
                "Error message should indicate that username does not match");
    }

    @Test
    @DisplayName("Logout with valid authToken")
    public void logoutValidToken(){

        /** When **/
        RequestResult.RegisterResult registerResult = userServiceInstance.registerUser(registerRequest);
        RequestResult.LoginResult loginResult = userServiceInstance.login(loginRequest);

        RequestResult.LogoutRequest logoutRequest = new RequestResult.LogoutRequest(loginResult.authToken());

        RequestResult.LogoutResult logoutResult = userServiceInstance.logout(logoutRequest);

        /** Then **/
        Assertions.assertNotNull(registerResult, "Register Result should not be null");
        Assertions.assertNotNull(loginResult, "Login result should not be null");
        Assertions.assertNull(logoutResult, "Logout result should return null");
    }

    @Test
    @DisplayName("Logout with invalid authToken")
    public void logoutInvalidToken(){

        /** When **/
        RequestResult.RegisterResult registerResult = userServiceInstance.registerUser(registerRequest);
        RequestResult.LoginResult loginResult = userServiceInstance.login(loginRequest);

        RequestResult.LogoutRequest logoutRequest = new RequestResult.LogoutRequest(loginResult.authToken()+1);

        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                userServiceInstance.logout(logoutRequest));;

        /** Then **/
        Assertions.assertNotNull(registerResult, "Register Result should not be null");
        Assertions.assertNotNull(loginResult, "Login result should not be null");
        Assertions.assertEquals("AuthToken is invalid", exception.getMessage(),
                "Error message should indicate that authToken is invalid");
    }

}
