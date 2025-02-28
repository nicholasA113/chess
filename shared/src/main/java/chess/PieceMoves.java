package chess;

import java.util.ArrayList;

public class PieceMoves {

    public void kingKnightMoves(int[] rowMoves, int[] colMoves,
                                ChessPosition myPosition, ChessBoard board,
                                ArrayList<ChessMove> moves){
        for (int i=0; i<rowMoves.length; i++){
            int currentRow = myPosition.getRow();
            int currentCol = myPosition.getColumn();

            currentRow += rowMoves[i];
            currentCol += colMoves[i];
            if (currentRow < 1 || currentRow > 8 || currentCol < 1 || currentCol > 8){
                continue;
            }
            ChessPiece chessPiece = board.getPiece(new ChessPosition(currentRow, currentCol));
            if (chessPiece == null){
                moves.add(new ChessMove(myPosition,
                        new ChessPosition(currentRow, currentCol),
                        null));
            } else if (chessPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()){
                moves.add(new ChessMove(myPosition,
                        new ChessPosition(currentRow, currentCol),
                        null));
            }
        }
    }

    public void queenBishopRookMoves(int[] rowMoves, int[] colMoves,
                                 ChessPosition myPosition, ChessBoard board,
                                 ArrayList<ChessMove> moves){
        for (int i = 0; i < rowMoves.length; i++) {
            int currentRow = myPosition.getRow();
            int currentCol = myPosition.getColumn();

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
