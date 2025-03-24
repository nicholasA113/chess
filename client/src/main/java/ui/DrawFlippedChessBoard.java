package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawFlippedChessBoard {

    //ChessBoard Dimensions
    private static final int BOARD_SIZE = 10;

    public static void main(String[] args) {
        DrawFlippedChessBoard chessBoard = new DrawFlippedChessBoard();
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);;
        chessBoard.drawBoard(out);
    }

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


    public void addNumbersLetters(PrintStream out, int i, int j){
        out.print(RESET_BG_COLOR);
        out.print(SET_BG_COLOR_LIGHT_GREY);
        switch(j){
            case (0), (9) -> {
                if (i >= 1 && i <= 8){
                    switch(i){
                        case (1) -> out.print(" 1 ");
                        case (2) -> out.print(" 2 ");
                        case (3) -> out.print(" 3 ");
                        case (4) -> out.print(" 4 ");
                        case (5) -> out.print(" 5 ");
                        case (6) -> out.print(" 6 ");
                        case (7) -> out.print(" 7 ");
                        case (8) -> out.print(" 8 ");
                    }
                }
                else{
                    out.print("   ");
                }
            }
            case (1) -> out.print(" h ");
            case (2) -> out.print(" g ");
            case (3) -> out.print(" f ");
            case (4) -> out.print(" e ");
            case (5) -> out.print(" d ");
            case (6) -> out.print(" c ");
            case (7) -> out.print(" b ");
            case (8) -> out.print(" a ");
        }
        out.print(RESET_BG_COLOR);
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

    public void addPieces(PrintStream out, int i, int j){
        if (i == 1){
            out.print(SET_TEXT_BOLD);
            out.print(SET_TEXT_COLOR_RED);
            switch (j){
                case (1), (8) -> out.print(" R ");
                case (2), (7) -> out.print(" N ");
                case (3), (6) -> out.print(" B ");
                case (4) -> out.print(" K ");
                case (5) -> out.print(" Q ");
            }
            out.print(RESET_TEXT_BOLD_FAINT);
            out.print(RESET_TEXT_COLOR);
        }
        else if (i == 2){
            if (j >= 1 && j<=8){
                out.print(SET_TEXT_COLOR_RED);
                out.print(SET_TEXT_BOLD);
                out.print(" P ");
            }
            out.print(RESET_TEXT_BOLD_FAINT);
        }
        else if (i==7){
            if (j >= 1 && j<=8){
                out.print(SET_TEXT_COLOR_BLUE);
                out.print(SET_TEXT_BOLD);
                out.print(" P ");
            }
            out.print(RESET_TEXT_BOLD_FAINT);
        }
        else if (i == 8){
            out.print(SET_TEXT_BOLD);
            out.print(SET_TEXT_COLOR_BLUE);
            switch (j){
                case (1), (8) -> out.print(" R ");
                case (2), (7) -> out.print(" N ");
                case (3), (6) -> out.print(" B ");
                case (4) -> out.print(" K ");
                case (5) -> out.print(" Q ");
            }
        }
        out.print(RESET_TEXT_BOLD_FAINT);
        out.print(RESET_TEXT_COLOR);
    }
}
