package chess;

import java.util.ArrayList;

public class KnightMoves {

    private final ChessBoard board;
    private final ChessPosition myPosition;
    PieceMoves pieceMoves = new PieceMoves();

    public KnightMoves(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    public void addMoves(ArrayList<ChessMove> moves) {
        int[] rowMoves = {2, 1, -1, -2, -2, -1, 1, 2};
        int[] colMoves = {1, 2, 2, 1, -1, -2, -2, -1};
        pieceMoves.kingKnightMoves(rowMoves, colMoves, myPosition, board, moves);
    }
}
