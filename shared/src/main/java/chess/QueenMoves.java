package chess;

import java.util.ArrayList;

public class QueenMoves {

    private final ChessBoard board;
    private final ChessPosition myPosition;
    PieceMoves pieceMoves = new PieceMoves();

    public QueenMoves(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    public void addMoves(ArrayList<ChessMove> moves) {
        int[] rowMoves = {1,1,0,-1,-1,-1,0,1};
        int[] colMoves = {0,1,1,1,0,-1,-1,-1};
        pieceMoves.queenBishopRookMoves(rowMoves, colMoves, myPosition, board, moves);
    }
}
