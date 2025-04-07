package websocket.commands;

import chess.ChessMove;
import chess.ChessPiece;
import model.GameData;

import java.util.List;

public class MakeMoveCommand extends UserGameCommand{

    private final ChessMove chessMove;

    private final ChessPiece chessPiece;

    public MakeMoveCommand(String authToken, Integer gameID, String username, boolean observer,
                           ChessMove chessMove, ChessPiece chessPiece, List<GameData> games,
                           String playerColor) {
        super(CommandType.MAKE_MOVE, authToken, observer, username, gameID, games, playerColor);
        this.chessMove = chessMove;
        this.chessPiece = chessPiece;
    }

    public ChessMove ChessMove() {
        return chessMove;
    }

    public ChessPiece ChessPiece() {
        return chessPiece;
    }

}
