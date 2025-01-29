package chess;

import java.util.ArrayList;

public class RookMoves {
    private ChessBoard board;
    private ChessPosition myPosition;

    public RookMoves(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    public void addMoves(ArrayList<ChessMove> moves){
        int[] rowMoves = {1,0,-1,0};
        int[] colMoves = {0,1,0,-1};

        for (int i=0; i<4; i++){
            int currentRow = myPosition.getRow();
            int currentCol = myPosition.getColumn();

            while (true){
                currentRow += rowMoves[i];
                currentCol += colMoves[i];
                if (currentRow < 1 || currentRow > 8 || currentCol < 1 || currentCol > 8){
                    break;
                }
                ChessPiece piece = board.getPiece(new ChessPosition(currentRow, currentCol));
                if (piece == null){
                    moves.add(new ChessMove(myPosition,
                            new ChessPosition(currentRow, currentCol),
                            null));
                }
                else if (piece.getTeamColor() != board.getPiece(myPosition).getTeamColor()){
                    moves.add(new ChessMove(myPosition,
                            new ChessPosition(currentRow, currentCol),
                            null));
                    break;
                } else{
                    break;
                }
            }
        }
    }
}
