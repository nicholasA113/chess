package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


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
        /** Given **/
        var registerServiceInstance = new UserService();
        var registerRequest = new RequestResult.RegisterRequest(
                null, null, "123456@gmail.com");

        /** When **/
        RequestResult.RegisterResult actualRegisterResult = registerServiceInstance.
                registerUser(registerRequest);

        /** Then **/
        Assertions.assertNull(actualRegisterResult, "Result should be null, even with some null inputs");
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
        RequestResult.RegisterResult registerResult1 = registerServiceInstance.
                registerUser(registerRequest1);
        RequestResult.RegisterResult registerResult2 = registerServiceInstance.
                registerUser(registerRequest2);

        /** Then **/
        Assertions.assertNotNull(registerResult1, "Register result 1 should not be null");
        Assertions.assertNull(registerResult2, "Register should be null because it used" +
                "the same username");
    }

}
