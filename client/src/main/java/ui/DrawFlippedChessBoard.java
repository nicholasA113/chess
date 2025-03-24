package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawFlippedChessBoard {

    //ChessBoard Dimensions
    private static final int BOARD_SIZE = 10;
    private static StringBuilder board = new StringBuilder();

    public static void main(String[] args) {
        DrawFlippedChessBoard flippedBoard = new DrawFlippedChessBoard();
        flippedBoard.drawBoard();
        System.out.print(flippedBoard.printBoard());
    }

    public String printBoard(){
        return board.toString();
    }

    public void drawBoard(){
        board.setLength(0);
        for (int i = 0; i< BOARD_SIZE; i++){
            for (int j = 0; j< BOARD_SIZE; j++){
                if (i==0 || i== BOARD_SIZE -1 || j==0 || j== BOARD_SIZE -1) {
                    addNumbersLetters(i, j);
                }
                else if (i==1 || i==2 || i==7 || i==8){
                    addColoredGrid(i, j);
                }
                else{
                    addColoredGrid(i, j);
                }
            }
            board.append("\n");
        }
    }


    public void addNumbersLetters(int i, int j){
        switch(j){
            case (0), (9) -> {
                if (i >= 1 && i <= 8){
                    switch(i){
                        case (1) -> board.append(SET_BG_COLOR_LIGHT_GREY).append(" 1 ");
                        case (2) -> board.append(SET_BG_COLOR_LIGHT_GREY).append(" 2 ");
                        case (3) -> board.append(SET_BG_COLOR_LIGHT_GREY).append(" 3 ");
                        case (4) -> board.append(SET_BG_COLOR_LIGHT_GREY).append(" 4 ");
                        case (5) -> board.append(SET_BG_COLOR_LIGHT_GREY).append(" 5 ");
                        case (6) -> board.append(SET_BG_COLOR_LIGHT_GREY).append(" 6 ");
                        case (7) -> board.append(SET_BG_COLOR_LIGHT_GREY).append(" 7 ");
                        case (8) -> board.append(SET_BG_COLOR_LIGHT_GREY).append(" 8 ");
                    }
                }
                else{
                    board.append(SET_BG_COLOR_LIGHT_GREY).append("   ").append(RESET_BG_COLOR);
                }
            }
            case (1) -> board.append(SET_BG_COLOR_LIGHT_GREY).append(" h ");
            case (2) -> board.append(SET_BG_COLOR_LIGHT_GREY).append(" g ");
            case (3) -> board.append(SET_BG_COLOR_LIGHT_GREY).append(" f ");
            case (4) -> board.append(SET_BG_COLOR_LIGHT_GREY).append(" e ");
            case (5) -> board.append(SET_BG_COLOR_LIGHT_GREY).append(" d ");
            case (6) -> board.append(SET_BG_COLOR_LIGHT_GREY).append(" c ");
            case (7) -> board.append(SET_BG_COLOR_LIGHT_GREY).append(" b ");
            case (8) -> board.append(SET_BG_COLOR_LIGHT_GREY).append(" a ");
        }
        board.append(RESET_BG_COLOR);
    }


    public void addColoredGrid(int i, int j){
        if ((i + j) % 2 == 0) {
            board.append(SET_BG_COLOR_BLACK);
        } else {
            board.append(SET_BG_COLOR_WHITE);
        }
        if (i == 1 || i == 2 || i == 7 || i == 8) {
            addPieces(i, j);
        } else {
            board.append(EMPTY);
        }
        board.append(RESET_BG_COLOR);
    }

    public void addPieces(int i, int j){
        if (i == 1){
            if (j == 1 || j==8){
                board.append(WHITE_ROOK);
            }
            else if (j==2 || j==7){
                board.append(WHITE_KNIGHT);
            }
            else if (j==3 || j==6){
                board.append(WHITE_BISHOP);
            }
            else if (j==4){
                board.append(WHITE_QUEEN);
            }
            else if (j==5){
                board.append(WHITE_KING);
            }
        }
        else if (i == 2){
            if (j >= 1 && j<=8){
                board.append(WHITE_PAWN);
            }
        }
        else if (i==7){
            if (j >= 1 && j<=8){
                board.append(BLACK_PAWN);
            }
        }
        else if (i == 8){
            if (j == 1 || j==8){
                board.append(BLACK_ROOK);
            }
            else if (j==2 || j==7){
                board.append(BLACK_KNIGHT);
            }
            else if (j==3 || j==6){
                board.append(BLACK_BISHOP);
            }
            else if (j==4){
                board.append(BLACK_QUEEN);
            }
            else if (j==5){
                board.append(BLACK_KING);
            }
        }
    }
}
