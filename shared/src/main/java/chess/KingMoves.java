package chess;

import java.util.ArrayList;

public class KingMoves {

    private final ChessPosition myPosition;
    private final ChessBoard board;
    PieceMoves pieceMoves = new PieceMoves();

    public KingMoves(ChessBoard board, ChessPosition myPosition) {
        this.myPosition = myPosition;
        this.board = board;
    }

    public void addMoves(ArrayList<ChessMove> moves){
        int[] rowMoves = {1,1,0,-1,-1,-1,0,1};
        int[] colMoves = {0,1,1,1,0,-1,-1,-1};
        pieceMoves.kingKnightMoves(rowMoves, colMoves, myPosition, board, moves);
    }
}
