package ui;

import static ui.EscapeSequences.*;

public abstract class DrawChessBoard {

    //ChessBoard Dimensions
    private static final int BOARD_SIZE = 10;

    public static StringBuilder[][] drawChessBoard(String playerColor, StringBuilder[][] chessBoard){
        if (playerColor.equalsIgnoreCase("WHITE")){
            DrawRegularChessBoard regularBoard = new DrawRegularChessBoard();
            regularBoard.drawBoard(chessBoard, playerColor);
        }
        else{
            DrawFlippedChessBoard flippedBoard = new DrawFlippedChessBoard();
            flippedBoard.drawBoard(chessBoard, playerColor);
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
    public abstract void printPieces(StringBuilder[][] chessBoard, int i, int j, String textColor, String bgColor);

    public void addPieces(StringBuilder[][] chessBoard, int i, int j, String playerColor, String bgColor){
        if (i == 1){
            if (playerColor.equalsIgnoreCase("WHITE")){
                printPieces(chessBoard, i, j, SET_TEXT_COLOR_BLUE, bgColor);
            }
            else{
                printPieces(chessBoard, i, j, SET_TEXT_COLOR_RED, bgColor);
            }
            chessBoard[i][j].append(RESET_TEXT_BOLD_FAINT);
            chessBoard[i][j].append(RESET_TEXT_COLOR);
        }
        else if (i == 2){
            printPawns(chessBoard, i, j, playerColor, SET_TEXT_COLOR_BLUE, SET_TEXT_COLOR_RED, bgColor);
        }
        else if (i==7){
            printPawns(chessBoard, i, j, playerColor, SET_TEXT_COLOR_RED, SET_TEXT_COLOR_BLUE, bgColor);
        }
        else if (i == 8){
            if (playerColor.equalsIgnoreCase("WHITE")){
                printPieces(chessBoard, i, j, SET_TEXT_COLOR_RED, bgColor);
            }
            else{
                printPieces(chessBoard, i, j, SET_TEXT_COLOR_BLUE, bgColor);
            }
        }
        chessBoard[i][j].append(RESET_TEXT_BOLD_FAINT);
        chessBoard[i][j].append(RESET_TEXT_COLOR);
    }

    private void printPawns(StringBuilder[][] chessBoard, int i, int j,
                            String playerColor, String setTextColorBlue,
                            String setTextColorRed, String bgColor) {
        chessBoard[i][j] = new StringBuilder();
        chessBoard[i][j].append(bgColor);
        if (j >= 1 && j<=8){
            if (playerColor.equalsIgnoreCase("WHITE")){
                chessBoard[i][j].append(setTextColorBlue);
            }
            else{
                chessBoard[i][j].append(setTextColorRed);
            }
            chessBoard[i][j].append(SET_TEXT_BOLD);
            chessBoard[i][j].append(" P ");
        }
        chessBoard[i][j].append(RESET_TEXT_BOLD_FAINT);
    }

    public void drawBoard(StringBuilder[][] chessBoard, String playerColor){
        for (int i = 0; i< BOARD_SIZE; i++){
            for (int j = 0; j< BOARD_SIZE; j++){
                chessBoard[i][j] = new StringBuilder();
                if (i==0 || i== BOARD_SIZE -1 || j==0 || j== BOARD_SIZE -1) {
                    addNumbersLetters(chessBoard, i, j);
                }
                else{
                    addColoredGrid(chessBoard, i, j, playerColor);
                }
            }
        }
    }

    public void addColoredGrid(StringBuilder[][] chessBoard, int i, int j, String playerColor){
        chessBoard[i][j] = new StringBuilder();
        String bgColor;

        if ((i + j) % 2 == 0) {
            bgColor = SET_BG_COLOR_WHITE;
        } else {
            bgColor = SET_BG_COLOR_BLACK;
        }

        if (i==1 || i==2 || i==7 || i==8){
            addPieces(chessBoard, i, j, playerColor, bgColor);
        }
        else{
            chessBoard[i][j].append(bgColor).append("   ");
        }
        chessBoard[i][j].append(RESET_BG_COLOR);
    }

    public static void highlightSpace(StringBuilder positionToHighlight, String bgColor) {
        positionToHighlight.setLength(0);
        positionToHighlight
                .append(bgColor)
                .append("   ")
                .append(RESET_BG_COLOR);
    }

}
