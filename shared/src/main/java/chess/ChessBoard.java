package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {}

    public ChessPiece[][] getBoard(){
        return board;
    }

    public ChessBoard copy(){
        ChessBoard backupBoard = new ChessBoard();
        for (int r=0; r<8; r++){
            for (int c=0; c<8; c++){
                if (board[r][c] != null){
                    ChessPiece piece = board[r][c];
                    ChessPiece newPiece = new ChessPiece(piece.getTeamColor(), piece.getPieceType());
                    backupBoard.addPiece(new ChessPosition(r+1, c+1), newPiece);
                }
            }
        }
        return backupBoard;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "board=" + Arrays.toString(board) +
                '}';
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int r = 1; r < 9; r++) {
            for (int c = 1; c < 9; c++) {
                if (r == 1 && (c == 1 || c == 8)) {
                    addPiece(new ChessPosition(r, c),
                            new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
                }
                if (r == 1 && (c == 2 || c == 7)) {
                    addPiece(new ChessPosition(r, c),
                            new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
                }
                if (r == 1 && (c == 3 || c == 6)) {
                    addPiece(new ChessPosition(r, c),
                            new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
                }
                if (r == 1 && c == 4) {
                    addPiece(new ChessPosition(r, c),
                            new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
                }
                if (r == 1 && c == 5) {
                    addPiece(new ChessPosition(r, c),
                            new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
                }
                if (r == 2) {
                    addPiece(new ChessPosition(r, c),
                            new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
                }
                if (r == 7) {
                    addPiece(new ChessPosition(r, c),
                            new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
                }
                if (r == 8 && (c == 1 || c == 8)) {
                    addPiece(new ChessPosition(r, c),
                            new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
                }
                if (r == 8 && (c == 2 || c == 7)) {
                    addPiece(new ChessPosition(r, c),
                            new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
                }
                if (r == 8 && (c == 3 || c == 6)) {
                    addPiece(new ChessPosition(r, c),
                            new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
                }
                if (r == 8 && c == 4) {
                    addPiece(new ChessPosition(r, c),
                            new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
                }
                if (r == 8 && c == 5) {
                    addPiece(new ChessPosition(r, c),
                            new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
                }
            }
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
