package chess;

import java.util.ArrayList;

public class BishopMoves {
    private final ChessPosition myPosition;
    private final ChessBoard board;
    PieceMoves pieceMoves = new PieceMoves();

    public BishopMoves(ChessBoard board, ChessPosition myPosition) {
        this.myPosition = myPosition;
        this.board = board;
    }

    public void addMoves(ArrayList<ChessMove> moves) {
        int[] rowMoves = {1, 1, -1, -1};
        int[] colMoves = {1, -1, 1, -1};
        pieceMoves.queenBishopRookMoves(rowMoves, colMoves, myPosition, board, moves);
    }
}