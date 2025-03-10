import dataaccess.*;
import server.Server;

import chess.*;

public class Main {

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        Server server = new Server();

        SQLUserDataDAO sqlUserDataDAO;
        SQLAuthDataDAO sqlAuthDataDAO;
        SQLGameDataDAO sqlGameDataDAO;

        try{
            sqlUserDataDAO = new SQLUserDataDAO();
            sqlAuthDataDAO = new SQLAuthDataDAO();
            sqlGameDataDAO = new SQLGameDataDAO();
        }
        catch (Exception e){
            System.out.println("Error getting sqlUSerDataDAO");
            return;
        }

        server.setUserDataAccess(sqlUserDataDAO);
        server.setAuthDataAccess(sqlAuthDataDAO);
        server.setGameDataAccess(sqlGameDataDAO);
        server.setClearService(sqlAuthDataDAO, sqlUserDataDAO, sqlGameDataDAO);

        server.run(8080);
    }
}