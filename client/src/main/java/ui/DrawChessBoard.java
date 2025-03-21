package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawChessBoard {

    //ChessBoard Dimensions
    private static final int BOARD_SIZE = 10;
    private static final String MOVE_TO_TOP = "\033[H";

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        DrawChessBoard board = new DrawChessBoard();
        board.drawBoard(out);
    }

    public void drawBoard(PrintStream out){
        out.print(MOVE_TO_TOP);
        for (int i = 0; i< BOARD_SIZE; i++){
            for (int j = 0; j< BOARD_SIZE; j++){
                if (i==0 || i== BOARD_SIZE -1 || j==0 || j== BOARD_SIZE -1) {
                    addNumbersLetters(out, i, j);
                }
                else{
                    if ((i+j)%2==0){
                        out.print(SET_BG_COLOR_WHITE + "   " + RESET_BG_COLOR);
                    }
                    else{
                        out.print(SET_BG_COLOR_BLACK + "   " + RESET_BG_COLOR);
                    }
                }
            }
            out.println();
        }
    }


    public void addNumbersLetters(PrintStream out, int i, int j){
        switch(j){
            case (0), (9) -> {
                if (i >= 1 && i <= 8){
                    switch(i){
                        case (1) -> {
                            out.print(SET_BG_COLOR_LIGHT_GREY);
                            out.print(" 1 ");
                        }
                        case (2) -> {
                            out.print(SET_BG_COLOR_LIGHT_GREY);
                            out.print(" 2 ");
                        }
                        case (3) -> {
                            out.print(SET_BG_COLOR_LIGHT_GREY);
                            out.print(" 3 ");
                        }
                        case (4) -> {
                            out.print(SET_BG_COLOR_LIGHT_GREY);
                            out.print(" 4 ");
                        }
                        case (5) -> {
                            out.print(SET_BG_COLOR_LIGHT_GREY);
                            out.print(" 5 ");
                        }
                        case (6) -> {
                            out.print(SET_BG_COLOR_LIGHT_GREY);
                            out.print(" 6 ");
                        }
                        case (7) -> {
                            out.print(SET_BG_COLOR_LIGHT_GREY);
                            out.print(" 7 ");
                        }
                        case (8) -> {
                            out.print(SET_BG_COLOR_LIGHT_GREY);
                            out.print(" 8 ");
                        }
                    }
                }
                else{
                    out.print(SET_BG_COLOR_LIGHT_GREY + "   " + RESET_BG_COLOR);
                }
            }
            case (1) -> {
                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(" a ");
            }
            case (2) -> {
                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(" b ");
            }
            case (3) -> {
                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(" c ");
            }
            case (4) -> {
                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(" d ");
            }
            case (5) -> {
                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(" e ");
            }
            case (6) -> {
                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(" f ");
            }
            case (7) -> {
                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(" g ");
            }
            case (8) -> {
                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(" h ");
            }
        }
        out.print(RESET_BG_COLOR);
    }


    public void addPieces(){
        System.out.print(MOVE_TO_TOP);
        System.out.print("   ");
    }
}
