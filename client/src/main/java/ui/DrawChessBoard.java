package ui;

import static ui.EscapeSequences.*;

public class DrawChessBoard {

    //ChessBoard Dimensions
    private static final int BOARD_SIZE_LENGTH_HEIGHT = 10;

    public void drawBoard(){
        for (int i=0; i<BOARD_SIZE_LENGTH_HEIGHT; i++){
            for (int j=0; j<BOARD_SIZE_LENGTH_HEIGHT; j++){
                if (i==0 || i==BOARD_SIZE_LENGTH_HEIGHT-1 || j==0 || j==BOARD_SIZE_LENGTH_HEIGHT-1) {
                    if (i == 0 || i == BOARD_SIZE_LENGTH_HEIGHT - 1) {
                        System.out.print(SET_BG_COLOR_LIGHT_GREY + " ");
                        switch (j) {
                            case (1) -> System.out.print("a");
                            case (2) -> System.out.print("b");
                            case (3) -> System.out.print("c");
                            case (4) -> System.out.print("d");
                            case (5) -> System.out.print("e");
                            case (6) -> System.out.print("f");
                            case (7) -> System.out.print("g");
                            case (8) -> System.out.print("h");
                        }
                        System.out.print(SET_BG_COLOR_LIGHT_GREY + " ");
                    }
                    else{
                        System.out.print(SET_BG_COLOR_LIGHT_GREY + "   " + RESET_BG_COLOR);
                    }
                }
                else if (i == 1 && (j >= 1 && i <= j)){
                    System.out.print(BLACK_PAWN);
                }
                else{
                    if ((i+j)%2==0){
                        System.out.print(SET_BG_COLOR_WHITE + "   " + RESET_BG_COLOR);
                    }
                    else{
                        System.out.print(SET_BG_COLOR_BLACK + "   " + RESET_BG_COLOR);
                    }
                }
            }
            System.out.println();
        }
    }
}
