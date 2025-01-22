package chess;

import java.util.ArrayList;

public class PawnMoves {

    private ChessBoard board;
    private ChessPosition myPosition;

    public PawnMoves(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    public void addMoves(ArrayList<ChessMove> moves) {
        int currentRow = myPosition.getRow();
        int currentColumn = myPosition.getColumn();
        ChessPiece piece = board.getPiece(myPosition);

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && currentRow < 6) {
            currentRow += 1;
            ChessPiece currentPiece = board.getPiece(new ChessPosition(currentRow, currentColumn));
            if (currentPiece == null) {
                moves.add(new ChessMove(myPosition,
                        new ChessPosition(currentRow, currentColumn),
                        null));
            }
            int[] colMoves = {1, -2};
            for (int i = 0; i < colMoves.length; i++) {
                currentColumn += colMoves[i];
                if (currentRow < 1 || currentRow > 8 || currentColumn < 1 || currentColumn > 8){
                    continue;
                }
                ChessPiece possibleEnemyPiece = board.getPiece(new ChessPosition(currentRow, currentColumn));
                if (possibleEnemyPiece == null){
                    continue;
                }
                if (possibleEnemyPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    moves.add(new ChessMove(myPosition,
                            new ChessPosition(currentRow, currentColumn),
                            null));
                }
            }
            if (currentPiece != null) {
                return;
            }
            if (myPosition.getRow() == 2) {
                currentRow += 1;
                currentColumn += 1;
                ChessPiece currentPiece2 = board.getPiece(new ChessPosition(currentRow, currentColumn));
                if (currentPiece2 != null) {
                    return;
                }
                moves.add(new ChessMove(myPosition,
                        new ChessPosition(currentRow, currentColumn),
                        null));
            }
        } else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && currentRow == 7) {
            currentRow += 1;
            for (ChessPiece.PieceType chessPiece : ChessPiece.PieceType.values()) {
                if (chessPiece == ChessPiece.PieceType.QUEEN || chessPiece == ChessPiece.PieceType.BISHOP ||
                        chessPiece == ChessPiece.PieceType.KNIGHT || chessPiece == ChessPiece.PieceType.ROOK) {
                    moves.add(new ChessMove(myPosition,
                            new ChessPosition(currentRow, currentColumn),
                            chessPiece));
                }
            }
        } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK && currentRow > 3) {
            currentRow -= 1;
            ChessPiece currentPiece = board.getPiece(new ChessPosition(currentRow, currentColumn));
            if (currentPiece == null) {
                moves.add(new ChessMove(myPosition,
                        new ChessPosition(currentRow, currentColumn),
                        null));
            }
            int[] colMoves = {1, -2};
            for (int i = 0; i < colMoves.length; i++) {
                currentColumn += colMoves[i];
                if (currentRow < 1 || currentRow > 8 || currentColumn < 1 || currentColumn > 8){
                    continue;
                }
                ChessPiece possibleEnemyPiece = board.getPiece(new ChessPosition(currentRow, currentColumn));
                if (possibleEnemyPiece == null){
                    continue;
                }
                if (possibleEnemyPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    moves.add(new ChessMove(myPosition,
                            new ChessPosition(currentRow, currentColumn),
                            null));
                }
            }
            if (currentPiece != null) {
                return;
            }
            if (myPosition.getRow() == 7) {
                currentRow -= 1;
                currentColumn += 1;
                ChessPiece blockedPiece2 = board.getPiece(new ChessPosition(currentRow, currentColumn));
                if (blockedPiece2 != null) {
                    return;
                }
                moves.add(new ChessMove(myPosition,
                        new ChessPosition(currentRow, currentColumn),
                        null));
            }
        } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK && currentRow == 2) {
            currentRow -= 1;
            int[] columnMoves = {1, -2};
            for (int i = 0; i < 2; i++) {
                currentColumn += columnMoves[i];
                ChessPiece possibleEnemyPiece = board.getPiece(new ChessPosition(currentRow, currentColumn));
                if (possibleEnemyPiece == null) {
                    continue;
                }
                if (possibleEnemyPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
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
            currentColumn += 1;
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