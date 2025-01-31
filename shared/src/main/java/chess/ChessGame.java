package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board = new ChessBoard();
    private TeamColor team = TeamColor.WHITE;

    public ChessGame() {
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }

    public void switchTeam(TeamColor team) {
        if (team == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", team=" + team +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && team == chessGame.team;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, team);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        ChessPiece chessPiece = board.getPiece(startPosition);
        if (chessPiece == null) {
            return validMoves;
        }
        validMoves.addAll(chessPiece.pieceMoves(board, startPosition));
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece chessPiece = board.getPiece(startPosition);

        Collection<ChessMove> valid_Moves = validMoves(startPosition);
        if (!valid_Moves.contains(move)) {
            throw new InvalidMoveException("Not a valid move");
        }
        if (chessPiece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("Not the current team's turn");
        }
        if (endPosition.getRow() == 1 || endPosition.getRow() == 8) {
            if (chessPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
                ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
                board.addPiece(endPosition, new ChessPiece(team, promotionPiece));
                board.addPiece(startPosition, null);
            } else {
                board.addPiece(endPosition, chessPiece);
                board.addPiece(startPosition, null);
            }
        } else {
            board.addPiece(endPosition, chessPiece);
            board.addPiece(startPosition, null);
        }
        switchTeam(team);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(teamColor);

        Collection<ChessMove> otherTeamMoves = getOtherTeamMoves(teamColor);

        boolean isInCheck = false;
        for (ChessMove otherTeamMove : otherTeamMoves) {
            if (otherTeamMove.getEndPosition().equals(kingPosition)) {
                isInCheck = true;
                break;
            }
        }
        return isInCheck;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(teamColor);
        Collection<ChessMove> otherTeamMoves = getOtherTeamMoves(teamColor);

        Collection<ChessMove> kingMoves = validMoves(kingPosition);

        Collection<ChessPosition> kingEndPosition = new ArrayList<>();
        Collection<ChessPosition> otherTeamEndPositions = new ArrayList<>();
        for (ChessMove kingMove : kingMoves){
            ChessPosition endPosition = kingMove.getEndPosition();
            kingEndPosition.add(endPosition);
        }
        for (ChessMove otherTeamMove : otherTeamMoves){
            ChessPosition endPosition = otherTeamMove.getEndPosition();
            otherTeamEndPositions.add(endPosition);
        }

        boolean inCheckMate = false;
        if (otherTeamEndPositions.containsAll(kingEndPosition)){
            inCheckMate = true;
        }
        return inCheckMate;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public ChessPosition getKingPosition(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        for (int r = 1; r < 9; r++) {
            for (int c = 1; c < 9; c++) {
                ChessPiece chessPiece = board.getPiece(new ChessPosition(r, c));
                if (chessPiece == null) {
                    continue;
                }
                if (chessPiece.getPieceType() == ChessPiece.PieceType.KING &&
                        chessPiece.getTeamColor() == teamColor) {
                    kingPosition = new ChessPosition(r, c);
                    break;
                }
            }
            if (kingPosition != null) {
                break;
            }
        }
        return kingPosition;
    }


    public Collection<ChessMove> getOtherTeamMoves(TeamColor teamColor){
        Collection<ChessMove> otherTeamMoves = new ArrayList<>();
        for (int r = 1; r < 9; r++) {
            for (int c = 1; c < 9; c++) {
                ChessPiece chessPiece = board.getPiece(new ChessPosition(r, c));
                if (chessPiece == null) {
                    continue;
                }
                if (chessPiece.getTeamColor() != teamColor) {
                    ChessPosition piecePosition = new ChessPosition(r, c);
                    Collection<ChessMove> validMoves = validMoves(piecePosition);
                    otherTeamMoves.addAll(validMoves);
                }
            }
        }
        return otherTeamMoves;
    }
}
