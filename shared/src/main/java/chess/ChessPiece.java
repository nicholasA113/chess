package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor teamColor;
    private ChessPiece.PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.pieceType = type;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "teamColor=" + teamColor +
                ", pieceType=" + pieceType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        switch (this.pieceType) {
            case PAWN:
                PawnMoves pawnMoves = new PawnMoves(board, myPosition);
                pawnMoves.addMoves(moves);
                break;
            case ROOK:
                RookMoves rookMoves = new RookMoves(board, myPosition);
                rookMoves.addMoves(moves);
                break;
            case QUEEN:
                QueenMoves queenMoves = new QueenMoves(board, myPosition);
                queenMoves.addMoves(moves);
                break;
            case KNIGHT:
                KnightMoves knightMoves = new KnightMoves(board, myPosition);
                knightMoves.addMoves(moves);
                break;
            case KING:
                KingMoves kingMoves = new KingMoves(board, myPosition);
                kingMoves.addMoves(moves);
                break;
            case BISHOP:
                BishopMoves bishopMoves = new BishopMoves(board, myPosition);
                bishopMoves.addMoves(moves);
                break;
        }
        return moves;
    }
}
