package websocket.commands;

import chess.ChessMove;
import model.GameData;

import java.util.List;

public class MakeMoveCommand extends UserGameCommand{

    private final ChessMove move;

    public MakeMoveCommand(String authToken, Integer gameID, String username, boolean observer, ChessMove move, List<GameData> games,
                           String playerColor) {
        super(CommandType.MAKE_MOVE, authToken, observer, username, gameID, games, playerColor);
        this.move = move;
    }

    public ChessMove getChessMove() {
        return move;
    }

}
