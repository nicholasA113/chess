package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.util.Map;

import static ui.EscapeSequences.*;

public class DrawRegularChessBoard extends DrawChessBoard{

    public void addNumbersLetters(StringBuilder[][] chessBoard, int i, int j){
        chessBoard[i][j] = new StringBuilder();
        chessBoard[i][j].append(RESET_BG_COLOR);
        chessBoard[i][j].append(SET_BG_COLOR_LIGHT_GREY);
        switch(j){
            case (0), (9) -> {
                if (i >= 1 && i <= 8){
                    switch(i){
                        case (1) -> chessBoard[i][j].append(" 8 ");
                        case (2) -> chessBoard[i][j].append(" 7 ");
                        case (3) -> chessBoard[i][j].append(" 6 ");
                        case (4) -> chessBoard[i][j].append(" 5 ");
                        case (5) -> chessBoard[i][j].append(" 4 ");
                        case (6) -> chessBoard[i][j].append(" 3 ");
                        case (7) -> chessBoard[i][j].append(" 2 ");
                        case (8) -> chessBoard[i][j].append(" 1 ");
                    }
                    chessBoard[i][j].append(RESET_BG_COLOR);
                }
                else{
                    chessBoard[i][j].append("   ");
                }
            }
            case (1) -> chessBoard[i][j].append(" a ");
            case (2) -> chessBoard[i][j].append(" b ");
            case (3) -> chessBoard[i][j].append(" c ");
            case (4) -> chessBoard[i][j].append(" d ");
            case (5) -> chessBoard[i][j].append(" e ");
            case (6) -> chessBoard[i][j].append(" f ");
            case (7) -> chessBoard[i][j].append(" g ");
            case (8) -> chessBoard[i][j].append(" h ");
        }
        chessBoard[i][j].append(RESET_BG_COLOR);
    }
}
