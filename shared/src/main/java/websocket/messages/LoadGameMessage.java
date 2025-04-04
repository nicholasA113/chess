package websocket.messages;

import chess.ChessGame;
import model.GameData;
import requestresultrecords.RequestResult;
import server.ServerFacade;

import java.util.List;

public class LoadGameMessage extends ServerMessage {

    ChessGame game;

    public LoadGameMessage(ChessGame game){
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public ChessGame getChessGame() {
        return game;
    }

}
