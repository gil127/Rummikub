package gameManagerServer;

import dataObject.Action;
import dataObject.Cube;
import converter.Converter;
import fileManager.EnumsForFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import rummikubLogic.Player;
import rummikubLogic.RummikubGameLogic;
import ws.rummikub.DuplicateGameName_Exception;
import ws.rummikub.Event;
import ws.rummikub.GameDetails;
import ws.rummikub.GameDoesNotExists_Exception;
import ws.rummikub.InvalidParameters_Exception;
import ws.rummikub.InvalidXML_Exception;
import ws.rummikub.PlayerDetails;
import ws.rummikub.Tile;

public class GameManagerServer {
    private List<RummikubGameLogic> allGames = new ArrayList<RummikubGameLogic>();

    public String createGameFromXML(String path) throws DuplicateGameName_Exception, InvalidParameters_Exception, InvalidXML_Exception {
        RummikubGameLogic game = new RummikubGameLogic();
        EnumsForFile.FileLoadResults fileLoadResult = game.loadSavedGame(path);
        
        if (fileLoadResult == EnumsForFile.FileLoadResults.FILE_NOT_VALID) {
            throw new InvalidParameters_Exception("The paramters in the file are invalid!", null);
        }
        else if (fileLoadResult == EnumsForFile.FileLoadResults.FILE_NOT_FOUND) {
            throw new InvalidXML_Exception("The file not found!", null);
        }
        else if (fileLoadResult == EnumsForFile.FileLoadResults.FILE_VALID) {
            if (allGames.contains(game)) {
                throw new DuplicateGameName_Exception("There is already game with the same name " + game.getGameName(), null);
            }
        }

        allGames.add(game);
        return game.getGameName();
    }

    public List<PlayerDetails> getPlayersDetails(String gameName) throws GameDoesNotExists_Exception {
        RummikubGameLogic game = getGameFromGameName(gameName);
        
        if (game == null) {
            throw new GameDoesNotExists_Exception("There is no game with the name " + gameName, null);
        }
        
        List<Player> players = game.getPlayers();
        
        return Converter.convertPlayersToPlayersDetails(players);
    }

    public void createGame(String name, int humanPlayers, int computerizedPlayers) throws InvalidParameters_Exception, DuplicateGameName_Exception {
        RummikubGameLogic game = getGameFromGameName(name);
        int numOfPlayers = computerizedPlayers + humanPlayers;
        
        if (game == null) {
            game = new RummikubGameLogic();
            if (numOfPlayers > game.getMaxAmountOfPlayers() ||
                numOfPlayers  < game.getMinAmountOfPlayers() ||
                    humanPlayers == 0) {
                throw new InvalidParameters_Exception("The number of players inserted is not valid", null);
            }
            game.setGameName(name);
            game.initiallizeNewGame(humanPlayers, computerizedPlayers);
            allGames.add(game);
        }
        else{
            throw new DuplicateGameName_Exception("There is already a game with the name " + name, null);
        }
    }

    public GameDetails getGameDetails(String gameName) throws GameDoesNotExists_Exception {
        RummikubGameLogic game = getGameFromGameName(gameName);
        
        if (game == null) {
            throw new GameDoesNotExists_Exception("There is no game with the name " + gameName, null);
        }
        
        return Converter.convertGameToGameDetails(game);
    }

    public List<String> getWaitingGames() {
        List<String> res = new ArrayList<String>();
        
        for (RummikubGameLogic game : allGames) {
            if (game.getGameStatus() == RummikubGameLogic.GameStatus.WAITING) {
                res.add(game.getGameName());
            }
        }
        
        return res;
    }

    public int joinGame(String gameName, String playerName) throws GameDoesNotExists_Exception, InvalidParameters_Exception {
        RummikubGameLogic game = getGameFromGameName(gameName);
        
        if (game == null) {
            throw new GameDoesNotExists_Exception("There is no game with the name " + gameName, null);
        }
        
        int playerID = getPlayerID();
        
        if (game.getGameStatus() == RummikubGameLogic.GameStatus.WAITING) {
            game.addHumanPlayer(playerName, false, playerID); 
        }
        else {
            throw new InvalidParameters_Exception("The game is not in Waiting status", null);
        }
        
        return playerID;
    }

    public PlayerDetails getPlayerDetails(int playerId) throws InvalidParameters_Exception {
        Player player = getPlayerFromPlayerID(playerId);
        
        if (player == null) {
            throw new InvalidParameters_Exception("playerID does not exist", null);
        }
        else{
            return Converter.convertPlayerToPlayerDetails(player, true);
        }
    }
    
    public List<Event> getEvents(int playerId, int eventId) throws InvalidParameters_Exception {
        RummikubGameLogic game = getGameFromPlayerID(playerId);
        
        if (game == null) {
            throw new InvalidParameters_Exception("There is no player with that playerID", null);
        }
        return game.getEventsByEventID(eventId);
    }

    public void createSequence(int playerId, List<Tile> tiles) throws InvalidParameters_Exception {
        RummikubGameLogic game = getGameFromPlayerID(playerId);
        Action currAction = Converter.makeCreateSequenceAction(tiles);
        if (game == null) {
            throw new InvalidParameters_Exception("There is no player with that playerID", null);
        }
        
        game.doNewAction(currAction);
    }

    public void addTile(int playerId, Tile tile, int sequenceIndex, int sequencePosition) throws InvalidParameters_Exception {
        RummikubGameLogic game = getGameFromPlayerID(playerId);
        
        if (game == null) {
            throw new InvalidParameters_Exception("There is no player with that playerID", null);
        }
        else if (sequenceIndex <= 0 || sequenceIndex > game.getNumOfSerials()) {
            throw new InvalidParameters_Exception("The sequence index inserted is invalid", null);
        }
        else if (game.getCurrentPlayer().getID() != playerId) {
            throw new InvalidParameters_Exception("This not your turn", null);
        }
        
        ArrayList<Cube> serial = game.getCubesFromBoard().get(sequenceIndex - 1);
        
        Action action = Converter.makeInsertActionFromAddTileEvent(tile, serial, sequencePosition, sequenceIndex);
        
        game.doNewAction(action);
    }

    public void moveTile(int playerId, int sourceSequenceIndex, int sourceSequencePosition, int targetSequenceIndex, int targetSequencePosition) throws InvalidParameters_Exception {
        RummikubGameLogic game = getGameFromPlayerID(playerId);
        
        if (game == null) {
            throw new InvalidParameters_Exception("There is no player with that playerID", null);
        }
        
        ArrayList<Cube> sourceSerial = game.getCubesFromBoard().get(sourceSequenceIndex - 1);
        ArrayList<Cube> targetSerial;
        
        if (targetSequenceIndex >= 1 && targetSequenceIndex <= game.getCubesFromBoard().size()) {
            targetSerial = game.getCubesFromBoard().get(targetSequenceIndex - 1);
        }
        else if (targetSequenceIndex == game.getCubesFromBoard().size() + 1){
            targetSerial = new ArrayList<Cube>();
            game.getCubesFromBoard().add(targetSerial);
        }
        else {
            throw new InvalidParameters_Exception("The sequenceIndex inserted is invalid", null);
        }
        
        if (targetSequencePosition < 0 || targetSequencePosition > targetSerial.size()
                || sourceSequencePosition < 0 || sourceSequencePosition >= sourceSerial.size()) {
            throw new InvalidParameters_Exception("The sequencePosition inserted is invalid", null);
        }
        
        Action action = Converter.makeInsertActionFromMoveTileEvent(sourceSerial, sourceSequencePosition, sourceSequenceIndex, targetSerial, targetSequencePosition, targetSequenceIndex);
        
        game.doNewAction(action);
    }

    public void takeBackTile(int playerId, int sequenceIndex, int sequencePosition) throws InvalidParameters_Exception {
        RummikubGameLogic game = getGameFromPlayerID(playerId);
        
        if (game == null) {
            throw new InvalidParameters_Exception("There is no player with that playerID", null);
        }
        
        game.cancelAction(sequenceIndex, sequencePosition);
    }

    public void finishTurn(int playerId) throws InvalidParameters_Exception {
        RummikubGameLogic game = getGameFromPlayerID(playerId);
        
        if (game == null) {
            throw new InvalidParameters_Exception("There is no player with that playerID", null);
        }
        
        game.finishTurn();
    }
    
    public void resign(int playerId) throws InvalidParameters_Exception {
        RummikubGameLogic game = getGameFromPlayerID(playerId);
        
        if (game == null) {
            throw new InvalidParameters_Exception("There is no player with that playerID", null);
        }
        
        game.removePlayerFromGame();
    }
    
    private RummikubGameLogic getGameFromGameName(String gameName) {
        RummikubGameLogic res = null;
        
        for (RummikubGameLogic game : allGames) {
            if (game.getGameName().equals(gameName)) {
                res = game;
                break;
            }
        }
        
        return res;
    }

    private int getPlayerID() {
        boolean idAlreadyTaken = true;
        Random random = new Random();
        int playerID = -1;
        
        while (idAlreadyTaken) {
            playerID = random.nextInt(1000);
            
            idAlreadyTaken = isIDAlreadyTaken(playerID);
        }
        
        return playerID;
    }

    private boolean isIDAlreadyTaken(int playerID) {
        for (RummikubGameLogic game : allGames) {
            for(Player player : game.getPlayers()) {
                if (!player.isComputer() && player.getID() == playerID) {
                    return true;
                }
            }
        }
        
        return false;
    }

    private Player getPlayerFromPlayerID(int playerID) {
        for (RummikubGameLogic game : allGames) {
            for(Player player : game.getPlayers()) {
                if (!player.isComputer() && player.getID() == playerID) {
                    return player;
                }
            }
        }
        
        return null;
    }
    
    private RummikubGameLogic getGameFromPlayerID(int playerID) {
        for (RummikubGameLogic game : allGames) {
            for(Player player : game.getPlayers()) {
                if (!player.isComputer() && player.getID() == playerID) {
                    return game;
                }
            }
        }
        
        return null;
    }
}
