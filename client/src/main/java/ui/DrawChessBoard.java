package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public abstract class DrawChessBoard {

    //ChessBoard Dimensions
    private static final int BOARD_SIZE = 10;

    public static void drawChessBoard(String playerColor) {
        DrawRegularChessBoard regularBoard = new DrawRegularChessBoard();
        DrawFlippedChessBoard flippedBoard = new DrawFlippedChessBoard();
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);;
        if (playerColor.equalsIgnoreCase("WHITE")){
            regularBoard.drawBoard(out, playerColor);
        }
        else{
            flippedBoard.drawBoard(out, playerColor);
        }
    }

    public abstract void addNumbersLetters(PrintStream out, int i, int j);
    public abstract void printPieces(PrintStream out, int j, String textColor);

    public void addPieces(PrintStream out, int i, int j, String playerColor){
        if (i == 1){
            if (playerColor.equalsIgnoreCase("WHITE")){
                printPieces(out, j, SET_TEXT_COLOR_BLUE);
            }
            else{
                printPieces(out, j, SET_TEXT_COLOR_RED);
            }
            out.print(RESET_TEXT_BOLD_FAINT);
            out.print(RESET_TEXT_COLOR);
        }
        else if (i == 2){
            printPawns(out, j, playerColor, SET_TEXT_COLOR_BLUE, SET_TEXT_COLOR_RED);
        }
        else if (i==7){
            printPawns(out, j, playerColor, SET_TEXT_COLOR_RED, SET_TEXT_COLOR_BLUE);
        }
        else if (i == 8){
            if (playerColor.equalsIgnoreCase("WHITE")){
                printPieces(out, j, SET_TEXT_COLOR_RED);
            }
            else{
                printPieces(out, j, SET_TEXT_COLOR_BLUE);
            }
        }
        out.print(RESET_TEXT_BOLD_FAINT);
        out.print(RESET_TEXT_COLOR);
    }

    private void printPawns(PrintStream out, int j, String playerColor, String setTextColorBlue, String setTextColorRed) {
        if (j >= 1 && j<=8){
            if (playerColor.equalsIgnoreCase("WHITE")){
                out.print(setTextColorBlue);
            }
            else{
                out.print(setTextColorRed);
            }
            out.print(SET_TEXT_BOLD);
            out.print(" P ");
        }
        out.print(RESET_TEXT_BOLD_FAINT);
    }

    public void drawBoard(PrintStream out, String playerColor){
        for (int i = 0; i< BOARD_SIZE; i++){
            for (int j = 0; j< BOARD_SIZE; j++){
                if (i==0 || i== BOARD_SIZE -1 || j==0 || j== BOARD_SIZE -1) {
                    addNumbersLetters(out, i, j);
                }
                else{
                    addColoredGrid(out, i, j, playerColor);
                }
            }
            out.print("\n");
        }
    }

    public void addColoredGrid(PrintStream out, int i, int j, String playerColor){
        if ((i + j) % 2 == 0) {
            out.print(SET_BG_COLOR_WHITE);
        } else {
            out.print(SET_BG_COLOR_BLACK);
        }

        if (i==1 || i==2 || i==7 || i==8){
            addPieces(out, i, j, playerColor);
        }
        else{
            out.print("   ");
        }
        out.print(RESET_BG_COLOR);
    }

}
