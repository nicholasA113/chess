package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawRegularChessBoard extends DrawChessBoard{

    public void addNumbersLetters(PrintStream out, int i, int j){
        out.print(RESET_BG_COLOR);
        out.print(SET_BG_COLOR_LIGHT_GREY);
        switch(j){
            case (0), (9) -> {
                if (i >= 1 && i <= 8){
                    switch(i){
                        case (1) -> out.print(" 8 ");
                        case (2) -> out.print(" 7 ");
                        case (3) -> out.print(" 6 ");
                        case (4) -> out.print(" 5 ");
                        case (5) -> out.print(" 4 ");
                        case (6) -> out.print(" 3 ");
                        case (7) -> out.print(" 2 ");
                        case (8) -> out.print(" 1 ");
                    }
                }
                else{
                    out.print("   ");
                }
            }
            case (1) -> out.print(" a ");
            case (2) -> out.print(" b ");
            case (3) -> out.print(" c ");
            case (4) -> out.print(" d ");
            case (5) -> out.print(" e ");
            case (6) -> out.print(" f ");
            case (7) -> out.print(" g ");
            case (8) -> out.print(" h ");
        }
        out.print(RESET_BG_COLOR);
    }

    public void addPieces(PrintStream out, int i, int j){
        if (i == 1){
            out.print(SET_TEXT_BOLD);
            out.print(SET_TEXT_COLOR_BLUE);
            switch (j){
                case (1), (8) -> out.print(" R ");
                case (2), (7) -> out.print(" N ");
                case (3), (6) -> out.print(" B ");
                case (4) -> out.print(" Q ");
                case (5) -> out.print(" K ");
            }
            out.print(RESET_TEXT_BOLD_FAINT);
            out.print(RESET_TEXT_COLOR);
        }
        else if (i == 2){
            if (j >= 1 && j<=8){
                out.print(SET_TEXT_COLOR_BLUE);
                out.print(SET_TEXT_BOLD);
                out.print(" P ");
            }
            out.print(RESET_TEXT_BOLD_FAINT);
        }
        else if (i==7){
            if (j >= 1 && j<=8){
                out.print(SET_TEXT_COLOR_RED);
                out.print(SET_TEXT_BOLD);
                out.print(" P ");
            }
            out.print(RESET_TEXT_BOLD_FAINT);
        }
        else if (i == 8){
            out.print(SET_TEXT_BOLD);
            out.print(SET_TEXT_COLOR_RED);
            switch (j){
                case (1), (8) -> out.print(" R ");
                case (2), (7) -> out.print(" N ");
                case (3), (6) -> out.print(" B ");
                case (4) -> out.print(" Q ");
                case (5) -> out.print(" K ");
            }
        }
        out.print(RESET_TEXT_BOLD_FAINT);
        out.print(RESET_TEXT_COLOR);
    }
}
