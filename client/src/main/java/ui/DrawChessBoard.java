package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.HashMap;
import java.util.Map;

import static ui.EscapeSequences.*;

public abstract class DrawChessBoard {

    public static final int BOARD_SIZE = 10;
    public static Map<ChessPiece.PieceType, String> chessPieces = new HashMap<>() {{
        put(ChessPiece.PieceType.KING, " K ");
        put(ChessPiece.PieceType.QUEEN, " Q ");
        put(ChessPiece.PieceType.ROOK, " R ");
        put(ChessPiece.PieceType.BISHOP, " B ");
        put(ChessPiece.PieceType.KNIGHT, " N ");
        put(ChessPiece.PieceType.PAWN, " P ");
    }};

    public static StringBuilder[][] drawChessBoard(String playerColor,
                                                   StringBuilder[][] chessBoard,
                                                   ChessBoard game){
        if (playerColor.equalsIgnoreCase("WHITE")){
            DrawRegularChessBoard regularBoard = new DrawRegularChessBoard();
            regularBoard.drawBoard(chessBoard, game, playerColor);
        }
        else{
            DrawFlippedChessBoard flippedBoard = new DrawFlippedChessBoard();
            flippedBoard.drawBoard(chessBoard, game, playerColor);
        }
        return chessBoard;
    }

    public static void printBoard(StringBuilder[][] chessBoard){
        for (int i=0; i<BOARD_SIZE; i++){
            for (int j=0; j<BOARD_SIZE; j++){
                System.out.print(chessBoard[i][j]);
            }
            System.out.println();
        }
    }

    public abstract void addNumbersLetters(StringBuilder[][] chessBoard, int i, int j);

    public void drawBoard(StringBuilder[][] chessBoard, ChessBoard game, String playerColor) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                chessBoard[i][j] = new StringBuilder();
                if (i == 0 || i == BOARD_SIZE - 1 || j == 0 || j == BOARD_SIZE - 1) {
                    addNumbersLetters(chessBoard, i, j);
                } else {
                    int gameRow = playerColor.equalsIgnoreCase("white") ? 9 - i : i;
                    int gameCol = playerColor.equalsIgnoreCase("white") ? j : 9 - j;
                    if (gameRow >= 1 && gameRow <= 8 && gameCol >= 1 && gameCol <= 8) {
                        ChessPosition gamePosition = new ChessPosition(gameRow, gameCol);
                        addColoredGrid(chessBoard, i, j, game,
                                gamePosition, chessPieces, playerColor);
                    }
                }
            }
        }
    }

    public void addColoredGrid(StringBuilder[][] chessBoard, int i, int j,
                               ChessBoard game, ChessPosition gamePosition,
                               Map<ChessPiece.PieceType, String> chessPieces,
                               String playerColor) {
        chessBoard[i][j] = new StringBuilder();
        String bgColor;
        String textColor = "";
        if ((i + j) % 2 == 0) {
            bgColor = SET_BG_COLOR_WHITE;
        } else {
            bgColor = SET_BG_COLOR_BLACK;
        }
        ChessPiece piece = game.getPiece(gamePosition);
        if (piece == null) {
            chessBoard[i][j].append(bgColor).append("   ");
        } else {
            ChessPiece.PieceType pieceType = piece.getPieceType();
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                textColor = SET_TEXT_COLOR_RED;
            } else {
                textColor = SET_TEXT_COLOR_BLUE;
            }
            String pieceText = chessPieces.get(pieceType);
            chessBoard[i][j].append(bgColor).append(SET_TEXT_BOLD).append(textColor).append(pieceText);
        }
        chessBoard[i][j].append(RESET_BG_COLOR).append(RESET_TEXT_BOLD_FAINT).append(RESET_TEXT_COLOR);
    }

    public static void highlightSpace(StringBuilder positionToHighlight,
                                      String bgColor, int row, int col,
                                      ChessBoard board, String playerColor) {

        int gameRow = playerColor.equalsIgnoreCase("white") ? 9 - row : row;
        int gameCol = playerColor.equalsIgnoreCase("white") ? col : 9 - col;
        ChessPiece chessPiece = board.getPiece(new ChessPosition(gameRow, gameCol));

        positionToHighlight.setLength(0);
        if (chessPiece == null){
            positionToHighlight.append(RESET_BG_COLOR)
                    .append(bgColor)
                    .append("   ")
                    .append(RESET_BG_COLOR);
        }
        else{
            String text = chessPieces.get(chessPiece.getPieceType());
            positionToHighlight.append(RESET_TEXT_COLOR)
                    .append(bgColor)
                    .append(SET_TEXT_COLOR_BLACK)
                    .append(SET_TEXT_BOLD)
                    .append(text)
                    .append(RESET_TEXT_BOLD_FAINT)
                    .append(RESET_BG_COLOR)
                    .append(RESET_TEXT_COLOR);
        }
    }
}
