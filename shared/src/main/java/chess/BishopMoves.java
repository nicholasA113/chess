package chess;

import java.util.ArrayList;

public class BishopMoves {
    private ChessPosition myPosition;
    private ChessBoard board;

    public BishopMoves(ChessBoard board, ChessPosition myPosition) {
        this.myPosition = myPosition;
        this.board = board;
    }

    public void addMoves(ArrayList<ChessMove> moves) {
        int[] rowMoves = {1, 1, -1, -1};
        int[] colMoves = {1, -1, 1, -1};

        for (int i = 0; i < 4; i++) {
            int currentRow = myPosition.getRow();
            int currentCol = myPosition.getColumn();

            /** see if current position has a piece, and if it's a different
                team color **/

            while (true) {
                currentRow += rowMoves[i];
                currentCol += colMoves[i];
                if (currentRow < 1 || currentRow > 8 || currentCol < 1 || currentCol > 8) {
                    break;
                }
                ChessPiece chessPiece = board.getPiece(new ChessPosition(currentRow, currentCol));
                if (chessPiece == null) {
                    moves.add(new ChessMove(myPosition,
                            new ChessPosition(currentRow, currentCol),
                            null));
                }
                else if (chessPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    moves.add(new ChessMove(myPosition,
                            new ChessPosition(currentRow, currentCol),
                            null));
                    break;
                }
                else{
                    break;
                }
            }
        }
    }
}