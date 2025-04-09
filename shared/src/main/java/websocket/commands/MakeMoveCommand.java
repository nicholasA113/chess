package websocket.commands;

import chess.ChessMove;
import chess.ChessPiece;
import model.GameData;

import java.util.List;

public class MakeMoveCommand extends UserGameCommand {

    private ChessMove move;

    public MakeMoveCommand() {
        super(CommandType.MAKE_MOVE, null, null);
    }

    public MakeMoveCommand(String authToken, Integer gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
    }

    public ChessMove getChessMove() {
        return move;
    }

    public void setChessMove(ChessMove move) {
        this.move = move;
    }
}

