package service;

import dataaccess.InvalidInputException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServiceClassTests {

    @Test
    @DisplayName("Register Endpoint Valid Inputs")
    public void registerEndpointValid() {
        /** Given **/
        var registerServiceInstance = new UserService();
        var registerRequest = new RequestResult.RegisterRequest("nicholasUsername",
                "password", "123@456.com");

        /** When **/
        RequestResult.RegisterResult actualRegisterResult = registerServiceInstance.registerUser(registerRequest);

        /** Then **/
        Assertions.assertNotNull(actualRegisterResult, "The register result should not be null");
        Assertions.assertEquals("nicholasUsername", actualRegisterResult.username(), "Usernames should be the same");
        Assertions.assertNotNull(actualRegisterResult.authToken(), "AuthToken should not be null");
    }

    @Test
    @DisplayName("Register Endpoint Invalid Inputs")
    public void registerEndpointInvalid(){
        /**  Given **/
        var registerServiceInstance = new UserService();
        var registerRequest = new RequestResult.RegisterRequest(
                null, null, "123456@gmail.com");

        /** When **/
        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                registerServiceInstance.registerUser(registerRequest));

        /** Then **/
        Assertions.assertEquals("Username, password, or email must not be null", exception.getMessage(),
                "Exception message should indicate one of the one of the fields is missing");
    }

    @Test
    @DisplayName("User already taken")
    public void registerUserTaken(){
        /** Given**/
        var registerServiceInstance = new UserService();
        var registerRequest1 = new RequestResult.RegisterRequest(
                "nicholasUsername", "password", "123456@gmail.com");
        var registerRequest2 = new RequestResult.RegisterRequest(
                "nicholasUsername", "Password123", "nicholas@gmail.com");

        /** When **/
        RequestResult.RegisterResult registerResult1 = registerServiceInstance.registerUser(registerRequest1);
        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                registerServiceInstance.registerUser(registerRequest2));

        /** Then **/
        Assertions.assertNotNull(registerResult1, "First input should not be null");
        Assertions.assertEquals("Username is already taken", exception.getMessage(),
                "Error message should indicate that the username is already taken");
    }

    @Test
    @DisplayName("Login with valid credentials")
    public void loginValidInputs(){
        /** Given **/
        var loginServiceInstance = new UserService();
        var registerRequest = new RequestResult.RegisterRequest("nicholasUsername", "123456",
                "123@bmail.com");
        var loginRequest = new RequestResult.LoginRequest("nicholasUsername",
                "123456");

        /** When **/
        RequestResult.RegisterResult registerResult = loginServiceInstance.registerUser(registerRequest);
        RequestResult.LoginResult loginResult = loginServiceInstance.login(loginRequest);

        /** Then **/
        Assertions.assertNotNull(registerResult, "Register Result should not be null");
        Assertions.assertNotNull(loginResult, "Login Result should not be null");
        Assertions.assertEquals("nicholasUsername", loginResult.username(), "Usernames should match");
        Assertions.assertNotNull(loginResult.authToken(), "Auth Token should not be null");
    }

}
