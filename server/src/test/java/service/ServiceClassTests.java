package java.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.*;


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

}
