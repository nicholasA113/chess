package chess;

import java.util.ArrayList;

public class PawnMovesHelperFunctions {

    public void ifPossiblePieceNull(ChessBoard board, int currentRow,
                                    int currentCol, ArrayList<ChessMove> moves,
                                    ChessPosition myPosition, ChessPiece currentPiece){
        ChessPiece possiblePiece = board.getPiece(new ChessPosition(currentRow, currentCol));
        if (possiblePiece == null) {
            addPromotionPiece(moves, myPosition, currentRow, currentCol);
        }
        int[] colMoves = {1, -2};
        for (int colMove : colMoves) {
            currentCol += colMove;
            if (currentCol < 1 || currentCol > 8) {
                continue;
            }
            ChessPiece possiblePiece2 = board.getPiece(new ChessPosition(currentRow, currentCol));
            if (possiblePiece2 != null && possiblePiece2.getTeamColor() != currentPiece.getTeamColor()) {
                addPromotionPiece(moves, myPosition, currentRow, currentCol);
            }
        }
    }

    public void addPromotionPiece(ArrayList<ChessMove> moves, ChessPosition myPosition,
                                  int currentRow, int currentCol){
        for (ChessPiece.PieceType piece : ChessPiece.PieceType.values()) {
            if (piece == ChessPiece.PieceType.QUEEN || piece == ChessPiece.PieceType.BISHOP ||
                    piece == ChessPiece.PieceType.KNIGHT || piece == ChessPiece.PieceType.ROOK) {
                moves.add(new ChessMove(myPosition,
                        new ChessPosition(currentRow, currentCol),
                        piece));
            }
        }
    }

    public void addMiddleBoard(ChessPiece possiblePiece, ArrayList<ChessMove> moves,
                               ChessPosition myPosition, int currentRow, int currentCol,
                               ChessBoard board, ChessPiece currentPiece){
        if (possiblePiece == null) {
            moves.add(new ChessMove(myPosition,
                    new ChessPosition(currentRow, currentCol),
                    null));
        }
        int[] colMoves = {1, -2};
        for (int colMove : colMoves) {
            currentCol += colMove;
            if (currentCol < 1 || currentCol > 8) {
                continue;
            }
            ChessPiece possiblePiece2 = board.getPiece(new ChessPosition(currentRow, currentCol));
            if (possiblePiece2 != null && possiblePiece2.getTeamColor() != currentPiece.getTeamColor()) {
                moves.add(new ChessMove(myPosition,
                        new ChessPosition(currentRow, currentCol),
                        null));
            }
        }
    }

    public void promotionMove(int currentRow, int currentCol, ChessBoard board,
                              ArrayList<ChessMove> moves, ChessPosition myPosition){
        ChessPiece myPiece = board.getPiece(new ChessPosition(currentRow, currentCol));
        if (myPiece == null){
            return;
        }
        int[] colMoves = {1, -2};
        for (int colMove : colMoves) {
            currentCol += colMove;
            if (currentCol < 1 || currentCol > 8) {
                continue;
            }
            ChessPiece possiblePiece2 = board.getPiece(new ChessPosition(currentRow, currentCol));
            if (possiblePiece2 != null && possiblePiece2.getTeamColor() != myPiece.getTeamColor()) {
                moves.add(new ChessMove(myPosition,
                        new ChessPosition(currentRow, currentCol),
                        null));
            }
        }
    }
}
