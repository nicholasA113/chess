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
        int currentCol = myPosition.getColumn();
        ChessPiece currentPiece = board.getPiece(myPosition);

        if (currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (currentRow == 7) {
                currentRow += 1;
                ChessPiece possiblePiece = board.getPiece(new ChessPosition(currentRow, currentCol));
                if (possiblePiece == null) {
                    for (ChessPiece.PieceType piece : ChessPiece.PieceType.values()) {
                        if (piece == ChessPiece.PieceType.QUEEN || piece == ChessPiece.PieceType.BISHOP ||
                                piece == ChessPiece.PieceType.KNIGHT || piece == ChessPiece.PieceType.ROOK) {
                            moves.add(new ChessMove(myPosition,
                                    new ChessPosition(currentRow, currentCol),
                                    piece));
                        }
                    }
                }
                int[] colMoves = {1, -2};
                for (int colMove : colMoves) {
                    currentCol += colMove;
                    if (currentCol < 1 || currentCol > 8) {
                        continue;
                    }
                    ChessPiece possiblePiece2 = board.getPiece(new ChessPosition(currentRow, currentCol));
                    if (possiblePiece2 != null && possiblePiece2.getTeamColor() != currentPiece.getTeamColor()) {
                        for (ChessPiece.PieceType piece : ChessPiece.PieceType.values()) {
                            if (piece == ChessPiece.PieceType.QUEEN || piece == ChessPiece.PieceType.BISHOP ||
                                    piece == ChessPiece.PieceType.KNIGHT || piece == ChessPiece.PieceType.ROOK) {
                                moves.add(new ChessMove(myPosition,
                                        new ChessPosition(currentRow, currentCol),
                                        piece));
                            }
                        }
                    }
                }
            }

            if (currentRow > 2 && currentRow < 7) {
                currentRow += 1;
                ChessPiece possiblePiece = board.getPiece(new ChessPosition(currentRow, currentCol));
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
                int[] colMoves = {1, -2};
                for (int colMove : colMoves) {
                    currentCol += colMove;
                    if (currentCol < 1 || currentCol > 8) {
                        continue;
                    }
                    ChessPiece possiblePiece2 = board.getPiece(new ChessPosition(currentRow, currentCol));
                    if (possiblePiece2 != null) {
                        moves.add(new ChessMove(myPosition,
                                new ChessPosition(currentRow, currentCol),
                                null));
                    }
                }
            }
        }


        if (currentPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            if (currentRow == 2) {
                currentRow -= 1;
                ChessPiece possiblePiece = board.getPiece(new ChessPosition(currentRow, currentCol));
                if (possiblePiece == null) {
                    for (ChessPiece.PieceType piece : ChessPiece.PieceType.values()) {
                        if (piece == ChessPiece.PieceType.QUEEN || piece == ChessPiece.PieceType.BISHOP ||
                                piece == ChessPiece.PieceType.KNIGHT || piece == ChessPiece.PieceType.ROOK) {
                            moves.add(new ChessMove(myPosition,
                                    new ChessPosition(currentRow, currentCol),
                                    piece));
                        }
                    }
                }
                int[] colMoves = {1, -2};
                for (int colMove : colMoves) {
                    currentCol += colMove;
                    if (currentCol < 1 || currentCol > 8) {
                        continue;
                    }
                    ChessPiece possiblePiece2 = board.getPiece(new ChessPosition(currentRow, currentCol));
                    if (possiblePiece2 != null && possiblePiece2.getTeamColor() != currentPiece.getTeamColor()) {
                        for (ChessPiece.PieceType piece : ChessPiece.PieceType.values()) {
                            if (piece == ChessPiece.PieceType.QUEEN || piece == ChessPiece.PieceType.BISHOP ||
                                    piece == ChessPiece.PieceType.KNIGHT || piece == ChessPiece.PieceType.ROOK) {
                                moves.add(new ChessMove(myPosition,
                                        new ChessPosition(currentRow, currentCol),
                                        piece));
                            }
                        }
                    }
                }
            }
            if (currentRow > 2 && currentRow < 7) {
                currentRow -= 1;
                ChessPiece possiblePiece = board.getPiece(new ChessPosition(currentRow, currentCol));
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
                int[] colMoves = {1, -2};
                for (int colMove : colMoves) {
                    currentCol += colMove;
                    if (currentCol < 1 || currentCol > 8) {
                        continue;
                    }
                    ChessPiece possiblePiece2 = board.getPiece(new ChessPosition(currentRow, currentCol));
                    if (possiblePiece2 != null) {
                        moves.add(new ChessMove(myPosition,
                                new ChessPosition(currentRow, currentCol),
                                null));
                    }
                }
            }
        }
    }
}