package chess;

import java.util.ArrayList;

public class PawnMoves {

    private final ChessBoard board;
    private final ChessPosition myPosition;

    private final PawnMovesHelperFunctions pawnMovesHelperFunctions = new PawnMovesHelperFunctions();

    public PawnMoves(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    public void addMoves(ArrayList<ChessMove> moves) {
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        ChessPiece currentPiece = board.getPiece(myPosition);

        if (currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (currentRow == 7) {
                currentRow += 1;
                pawnMovesHelperFunctions.ifPossiblePieceNull(board, currentRow,
                        currentCol, moves, myPosition, currentPiece);
            }
            if (currentRow > 2 && currentRow < 7) {
                currentRow += 1;
                ChessPiece possiblePiece = board.getPiece(new ChessPosition(currentRow, currentCol));
                pawnMovesHelperFunctions.addMiddleBoard(possiblePiece, moves, myPosition, currentRow,
                        currentCol, board, currentPiece);
            }
            if (currentRow == 2) {
                for (int i = 0; i < 2; i++) {
                    currentRow += 1;
                    ChessPiece possiblePiece = board.getPiece(new ChessPosition(currentRow, currentCol));
                    if (possiblePiece == null) {
                        moves.add(new ChessMove(myPosition,
                                new ChessPosition(currentRow, currentCol),
                                null));
                    } else {
                        break;
                    }
                }
                currentRow -= 1;
                pawnMovesHelperFunctions.promotionMove(currentRow, currentCol, board, moves, myPosition);
            }
        }
        if (currentPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            if (currentRow == 2) {
                currentRow -= 1;
                pawnMovesHelperFunctions.ifPossiblePieceNull(board, currentRow,
                        currentCol, moves, myPosition, currentPiece);
            }
            if (currentRow > 2 && currentRow < 7) {
                currentRow -= 1;
                ChessPiece possiblePiece = board.getPiece(new ChessPosition(currentRow, currentCol));
                pawnMovesHelperFunctions.addMiddleBoard(possiblePiece, moves, myPosition, currentRow,
                        currentCol, board, currentPiece);
            }

            if (currentRow == 7) {
                for (int i = 0; i < 2; i++) {
                    currentRow -= 1;
                    ChessPiece possiblePiece = board.getPiece(new ChessPosition(currentRow, currentCol));
                    if (possiblePiece == null) {
                        moves.add(new ChessMove(myPosition,
                                new ChessPosition(currentRow, currentCol),
                                null));
                    } else {
                        break;
                    }
                }
                currentRow += 1;
                pawnMovesHelperFunctions.promotionMove(currentRow, currentCol, board, moves, myPosition);
            }
        }
    }
}