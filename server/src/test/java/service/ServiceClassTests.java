package java.service;

import chess.ChessGame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import passoff.chess.TestUtilities;
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


    @Test
    @DisplayName("Default Board No Statuses")
    public void noGameStatuses() {
        var game = new ChessGame();
        game.setBoard(TestUtilities.defaultBoard());
        game.setTeamTurn(ChessGame.TeamColor.WHITE);

        Assertions.assertFalse(game.isInCheck(ChessGame.TeamColor.BLACK), INCORRECT_BLACK_CHECK);
        Assertions.assertFalse(game.isInCheck(ChessGame.TeamColor.WHITE), INCORRECT_WHITE_CHECK);
        Assertions.assertFalse(game.isInCheckmate(ChessGame.TeamColor.BLACK), INCORRECT_BLACK_CHECKMATE);
        Assertions.assertFalse(game.isInCheckmate(ChessGame.TeamColor.WHITE), INCORRECT_WHITE_CHECKMATE);
        Assertions.assertFalse(game.isInStalemate(ChessGame.TeamColor.BLACK), INCORRECT_BLACK_STALEMATE);
        Assertions.assertFalse(game.isInStalemate(ChessGame.TeamColor.WHITE), INCORRECT_WHITE_STALEMATE);
    }

}
