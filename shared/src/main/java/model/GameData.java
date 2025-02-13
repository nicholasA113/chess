package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername,
                      String gameName, ChessGame game){

    public GameData updateBoard(ChessGame updatedGame){
        return new GameData(gameID, whiteUsername, blackUsername, gameName, updatedGame);

    }

}
