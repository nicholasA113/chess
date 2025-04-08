package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.DrawChessBoard.*;

import static ui.EscapeSequences.*;

public class DrawFlippedChessBoard extends DrawChessBoard{

    public void addNumbersLetters(StringBuilder[][] chessBoard, int i, int j){
        chessBoard[i][j] = new StringBuilder();
        chessBoard[i][j].append(RESET_BG_COLOR).append(SET_BG_COLOR_LIGHT_GREY);
        switch(j){
            case (0), (9) -> {
                if (i >= 1 && i <= 8){
                    switch(i){
                        case (1) -> chessBoard[i][j].append(" 1 ");
                        case (2) -> chessBoard[i][j].append(" 2 ");
                        case (3) -> chessBoard[i][j].append(" 3 ");
                        case (4) -> chessBoard[i][j].append(" 4 ");
                        case (5) -> chessBoard[i][j].append(" 5 ");
                        case (6) -> chessBoard[i][j].append(" 6 ");
                        case (7) -> chessBoard[i][j].append(" 7 ");
                        case (8) -> chessBoard[i][j].append(" 8 ");
                    }
                }
                else{
                    chessBoard[i][j].append("   ");
                }
            }
            case (1) -> chessBoard[i][j].append(" h ");
            case (2) -> chessBoard[i][j].append(" g ");
            case (3) -> chessBoard[i][j].append(" f ");
            case (4) -> chessBoard[i][j].append(" e ");
            case (5) -> chessBoard[i][j].append(" d ");
            case (6) -> chessBoard[i][j].append(" c ");
            case (7) -> chessBoard[i][j].append(" b ");
            case (8) -> chessBoard[i][j].append(" a ");
        }
        chessBoard[i][j].append(RESET_BG_COLOR);
    }
}
