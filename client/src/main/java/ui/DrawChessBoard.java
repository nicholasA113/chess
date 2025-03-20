package ui;

import static ui.EscapeSequences.*;

public class DrawChessBoard {

    //ChessBoard Dimensions
    private static final int BOARD_SIZE_LENGTH_HEIGHT = 10;
    private static final int SQUARE_SIZE_IN_BOARD = 1;

    public void drawBoard(){
        for (int i=0; i<BOARD_SIZE_LENGTH_HEIGHT; i++){
            for (int j=0; j<BOARD_SIZE_LENGTH_HEIGHT; j++){
                if (i==0 || i==BOARD_SIZE_LENGTH_HEIGHT-1 || j==0 || j==BOARD_SIZE_LENGTH_HEIGHT-1) {
                    System.out.print(SET_BG_COLOR_DARK_GREY + "   " + RESET_BG_COLOR);
                }
                else{
                    System.out.print("   ");
                }
            }
            System.out.println();
        }
    }
}
