/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rummikub.ws;

import gameManagerServer.GameManagerServer;
import javax.jws.WebService;
import ws.rummikub.DuplicateGameName_Exception;
import ws.rummikub.GameDoesNotExists_Exception;
import ws.rummikub.InvalidParameters_Exception;
import ws.rummikub.InvalidXML_Exception;


@WebService(serviceName = "RummikubWebServiceService", portName = "RummikubWebServicePort", endpointInterface = "ws.rummikub.RummikubWebService", targetNamespace = "http://rummikub.ws/", wsdlLocation = "WEB-INF/wsdl/RummikubGame/RummikubWebServiceService.wsdl")
public class RummikubGame {
    
    private gameManagerServer.GameManagerServer gameManager = new GameManagerServer();
    
    public java.util.List<ws.rummikub.Event> getEvents(int playerId, int eventId) throws InvalidParameters_Exception {
        return gameManager.getEvents(playerId, eventId);
    }

    public java.lang.String createGameFromXML(java.lang.String xmlData) throws DuplicateGameName_Exception, InvalidXML_Exception, InvalidParameters_Exception {
        return gameManager.createGameFromXML(xmlData);
    }

    public java.util.List<ws.rummikub.PlayerDetails> getPlayersDetails(java.lang.String gameName) throws GameDoesNotExists_Exception {
        return gameManager.getPlayersDetails(gameName);
    }

    public void createGame(java.lang.String name, int humanPlayers, int computerizedPlayers) throws DuplicateGameName_Exception, InvalidParameters_Exception {
        gameManager.createGame(name, humanPlayers, computerizedPlayers);
    }

    public ws.rummikub.GameDetails getGameDetails(java.lang.String gameName) throws GameDoesNotExists_Exception {
        return gameManager.getGameDetails(gameName);
    }

    public java.util.List<java.lang.String> getWaitingGames() {
        return gameManager.getWaitingGames();
    }

    public int joinGame(java.lang.String gameName, java.lang.String playerName) throws InvalidParameters_Exception, GameDoesNotExists_Exception {
        return gameManager.joinGame(gameName, playerName);
    }

    public ws.rummikub.PlayerDetails getPlayerDetails(int playerId) throws GameDoesNotExists_Exception, InvalidParameters_Exception {
        return gameManager.getPlayerDetails(playerId);
    }

    public void createSequence(int playerId, java.util.List<ws.rummikub.Tile> tiles) throws InvalidParameters_Exception {
        gameManager.createSequence(playerId, tiles);
    }

    public void addTile(int playerId, ws.rummikub.Tile tile, int sequenceIndex, int sequencePosition) throws InvalidParameters_Exception {
        gameManager.addTile(playerId, tile, sequenceIndex, sequencePosition);
    }

    public void takeBackTile(int playerId, int sequenceIndex, int sequencePosition) throws InvalidParameters_Exception {
        gameManager.takeBackTile(playerId, sequenceIndex, sequencePosition);
    }

    public void moveTile(int playerId, int sourceSequenceIndex, int sourceSequencePosition, int targetSequenceIndex, int targetSequencePosition) throws InvalidParameters_Exception {
        gameManager.moveTile(playerId, sourceSequenceIndex, sourceSequencePosition, targetSequenceIndex, targetSequencePosition);
    }

    public void finishTurn(int playerId) throws InvalidParameters_Exception {
        gameManager.finishTurn(playerId);
    }

    public void resign(int playerId) throws InvalidParameters_Exception {
        gameManager.resign(playerId);
    }
}
