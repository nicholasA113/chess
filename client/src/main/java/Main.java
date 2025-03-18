import chess.*;
import ui.ClientRepl;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        ClientRepl clientRepl = new ClientRepl(port);
        clientRepl.run();
    }
}