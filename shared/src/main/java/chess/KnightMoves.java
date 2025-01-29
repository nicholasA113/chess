package chess;

import java.util.ArrayList;

public class KnightMoves {

    private ChessBoard board;
    private ChessPosition myPosition;

    public KnightMoves(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    public void addMoves(ArrayList<ChessMove> moves) {
        int[] rowMoves = {2, 1, -1, -2, -2, -1, 1, 2};
        int[] colMoves = {1, 2, 2, 1, -1, -2, -2, -1};

        for (int i = 0; i < rowMoves.length; i++) {
            int currentRow = myPosition.getRow();
            int currentCol = myPosition.getColumn();

            currentRow += rowMoves[i];
            currentCol += colMoves[i];
            if (currentRow < 1 || currentRow > 8 || currentCol < 1 || currentCol > 8) {
                continue;
            }
            ChessPiece chessPiece = board.getPiece(new ChessPosition(currentRow, currentCol));
            if (chessPiece == null) {
                moves.add(new ChessMove(myPosition,
                        new ChessPosition(currentRow, currentCol),
                        null));
            } else if (chessPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition,
                        new ChessPosition(currentRow, currentCol),
                        null));
            }
        }
    }
}
