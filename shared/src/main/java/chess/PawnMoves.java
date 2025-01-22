package chess;

import java.util.ArrayList;

public class PawnMoves {

    private ChessBoard board;
    private ChessPosition myPosition;

    public PawnMoves(ChessBoard board, ChessPosition myPosition){
        this.board = board;
        this.myPosition = myPosition;
    }

    public void addMoves(ArrayList<ChessMove> moves){
        int currentRow = myPosition.getRow();
        int currentColumn = myPosition.getColumn();
        ChessPiece piece = board.getPiece(myPosition);

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && currentRow < 6){
            currentRow += 1;
            moves.add(new ChessMove(myPosition,
                    new ChessPosition(currentRow, currentColumn),
                    null));
            if (myPosition.getRow() == 2){
                currentRow += 1;
                moves.add(new ChessMove(myPosition,
                        new ChessPosition(currentRow, currentColumn),
                        null));
            }
        }
        else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && currentRow == 7){
            currentRow += 1;
            for (ChessPiece.PieceType chessPiece : ChessPiece.PieceType.values()) {
                if (chessPiece == ChessPiece.PieceType.QUEEN || chessPiece == ChessPiece.PieceType.BISHOP ||
                chessPiece == ChessPiece.PieceType.KNIGHT || chessPiece == ChessPiece.PieceType.ROOK) {
                    moves.add(new ChessMove(myPosition,
                            new ChessPosition(currentRow, currentColumn),
                            chessPiece));
                }
            }
        }
        else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK && currentRow > 3){
            currentRow -= 1;
            moves.add(new ChessMove(myPosition,
                    new ChessPosition(currentRow, currentColumn),
                    null));
            if (myPosition.getRow() == 7){
                currentRow -= 1;
                moves.add(new ChessMove(myPosition,
                        new ChessPosition(currentRow, currentColumn),
                        null));
            }
        }
        else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK && currentRow == 2){
            currentRow -= 1;
            for (ChessPiece.PieceType chessPiece : ChessPiece.PieceType.values()) {
                if (chessPiece == ChessPiece.PieceType.QUEEN || chessPiece == ChessPiece.PieceType.BISHOP ||
                chessPiece == ChessPiece.PieceType.KNIGHT || chessPiece == ChessPiece.PieceType.ROOK) {
                    moves.add(new ChessMove(myPosition,
                            new ChessPosition(currentRow, currentColumn),
                            chessPiece));
                }
            }
        }
    }
}
