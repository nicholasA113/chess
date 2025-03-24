package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public abstract class DrawChessBoard {

    //ChessBoard Dimensions
    private static final int BOARD_SIZE = 10;

    public static void main(String[] args) {
        DrawRegularChessBoard chessBoard = new DrawRegularChessBoard();
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);;
        chessBoard.drawBoard(out);
    }

    public abstract void addNumbersLetters(PrintStream out, int i, int j);
    public abstract void addPieces(PrintStream out, int i, int j);
    public abstract void printPieces(PrintStream out, int j, String textColor);

    public void drawBoard(PrintStream out){
        for (int i = 0; i< BOARD_SIZE; i++){
            for (int j = 0; j< BOARD_SIZE; j++){
                if (i==0 || i== BOARD_SIZE -1 || j==0 || j== BOARD_SIZE -1) {
                    addNumbersLetters(out, i, j);
                }
                else{
                    addColoredGrid(out, i, j);
                }
            }
            out.print("\n");
        }
    }

    public void addColoredGrid(PrintStream out, int i, int j){
        if ((i + j) % 2 == 0) {
            out.print(SET_BG_COLOR_WHITE);
        } else {
            out.print(SET_BG_COLOR_BLACK);
        }

        if (i==1 || i==2 || i==7 || i==8){
            addPieces(out, i, j);
        }
        else{
            out.print("   ");
        }
        out.print(RESET_BG_COLOR);
    }

}
