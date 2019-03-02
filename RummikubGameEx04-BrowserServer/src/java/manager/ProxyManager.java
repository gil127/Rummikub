package manager;

import converter.Converter;
import dataObject.Action;
import dataObject.ActionType;
import dataObject.Cube;
import dataObject.CubeBoard;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import rummikub.ws.DuplicateGameName_Exception;
import rummikub.ws.Event;
import rummikub.ws.EventType;
import rummikub.ws.GameDetails;
import rummikub.ws.GameDoesNotExists_Exception;
import rummikub.ws.GameStatus;
import rummikub.ws.InvalidParameters_Exception;
import rummikub.ws.InvalidXML_Exception;
import rummikub.ws.PlayerDetails;
import rummikub.ws.PlayerStatus;
import rummikub.ws.PlayerType;
import rummikub.ws.RummikubWebService;
import rummikub.ws.RummikubWebServiceService;
import rummikub.ws.Tile;

public class ProxyManager {
    private RummikubWebServiceService service;
    private RummikubWebService game;
    private int playerId;
    private int currentEventId;
    private boolean isGameOnWaiting;
    private String nameOfCurrGame;
    private String nameOfPlayer;
    private String winner;
    private boolean clientTurn;
    private Timer timer;
    private TimerTask timerTask;
    private boolean currentPlayerCanUseBoard;
    private boolean gameOver;
    private boolean playerRetired;
    private List<ArrayList<Cube>> demoBoard;
    private List<ArrayList<Cube>> backUpBoard;
    private String messageForClient;
    private List<Cube> playersCubes;
    
    public ProxyManager(){
        initialize();
        tryToConnectToServer();
    }
    
    private void initialize() {
        messageForClient = "";
        nameOfPlayer = "";
        nameOfCurrGame = "";
        this.playerId = -1;
        this.currentEventId = -1;
        isGameOnWaiting = true;
        clientTurn = false;
        demoBoard = new ArrayList<ArrayList<Cube>>();
        backUpBoard = new ArrayList<ArrayList<Cube>>();
        playersCubes = new ArrayList<Cube>();
        currentPlayerCanUseBoard = false;
        gameOver = false;
        this.playerRetired = false;
    }
    
    public void createGame(String nameOfGame, int numOfHumanPlayers, int numOfCompPlayers) throws DuplicateGameName_Exception, InvalidParameters_Exception {
        game.createGame(nameOfGame, numOfHumanPlayers, numOfCompPlayers);
    }
    
    public String loadGameFromXmlFile(StringBuilder fileContent) throws DuplicateGameName_Exception, InvalidParameters_Exception, InvalidXML_Exception {
        return game.createGameFromXML(createTempXMLFile(fileContent.toString()));
    }

    private String createTempXMLFile(String xmlData) {
        java.io.FileWriter fw;
        String path = "tempFile.xml";
        try {
            fw = new FileWriter(path);
            fw.write(xmlData);
            fw.close();
        } catch (IOException ex) {
        }
        return path;
    }
    
    public ArrayList<String> getWaitingHumanPlayersNames(String gameName) throws GameDoesNotExists_Exception {
        List<PlayerDetails> playersDetails = game.getPlayersDetails(gameName);
        ArrayList<String> res = new ArrayList<String>();
        
        for(PlayerDetails player : playersDetails) {
            if (player.getType() == PlayerType.HUMAN && player.getStatus() == PlayerStatus.ACTIVE) {
                res.add(player.getName());
            }
        }
        
        return res;
    }

    public String getWinnerName() {
        if (winner == null || winner.isEmpty()) {
            int i = 0;
            while (!gameOver && i < 3) {
                sleep();
                i++;
            }
        }
        if (winner == null || winner.isEmpty()) {
            return "";
        } else {
            return winner;
        }
    }

    public boolean isGameCanStart() {
        try {
            this.currentEventId = -1;
            List<Event> events = getEvents();
            
            for (Event event : events) {
                if (event.getType() == EventType.GAME_START) {
                    this.currentEventId = event.getId();
                    isGameOnWaiting = false;
                    setTimer();
                }
            }
        } catch (InvalidParameters_Exception ex) {
        }
        
        return !isGameOnWaiting;
    }

    public GameDetails getGameDetails(String gameName) throws GameDoesNotExists_Exception {
        return game.getGameDetails(gameName);
    }

    public List<String> getWaitingGames() {
        return game.getWaitingGames();
    }

    public boolean checkIfGameOver() {
        return gameOver || playerRetired;
    }

    private void tryToConnectToServer() {
        try {
            URL url = new URL("http://localhost:8080/RummikubGameEx04-LogicServer/RummikubWebServiceService");
            service = new RummikubWebServiceService(url);
            game = service.getRummikubWebServicePort();
        } catch (MalformedURLException ex) {
        }
    }
 
    public void sendActionToLogic(Action currAction) throws InvalidParameters_Exception {
        switch (currAction.getActionType()) {
            case ADD_TO_BOTTOM_EDGE : sendInsertActionToLogic(currAction);
                break;
            case ADD_TO_TOP_EDGE : sendInsertActionToLogic(currAction);
                break;
            case SPLIT : sendInsertActionToLogic(currAction);
                break;
            case CREATE_SERIAL : sendCreateSerialActionToLogic(currAction);
                break;
            case REPLACE_JOKER : sendReplaceJokerAcionToLogic(currAction);
                break;
            case TAKE_TILE_BACK : sendRegretActionToLogic(currAction);
                break;
        }
    }
    
    public String isCurrentPlayerTurn() {
        return Boolean.toString(clientTurn);
    }
    
    public void sleep() {
        long sleepTime = 1000l;
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ex) {

        }
    }
    
    public boolean isPlayerCanUseBoard() {
        return currentPlayerCanUseBoard;
    }
    
    public void currentPlayerRetired() throws InvalidParameters_Exception{
        clientTurn = false;
        setTimer();
        game.resign(playerId);
    }

    public boolean checkIfActionWith2Jokers(Action currentActionOfPlayer) {
        ArrayList<Cube> cubes = currentActionOfPlayer.getPlayerCubes();
        int numOfJokers = 0;
        
        for (Cube cube : cubes) {
            if (cube.isJoker()) {
                numOfJokers++;
            }
        }
        
        ArrayList<CubeBoard> cubesBoard = currentActionOfPlayer.getBoardCube();
        
        for (CubeBoard cubeBoard : cubesBoard) {
            if (cubeBoard.getCube().isJoker()) {
                numOfJokers++;
            }
        }
        
        return numOfJokers > 1;
    }

    public int getNumOfSerials() {
        return demoBoard.size();
    }
    
    public void joinGame(String gameName, String playerName) throws GameDoesNotExists_Exception, InvalidParameters_Exception {
        this.playerId = game.joinGame(gameName, playerName);
        this.nameOfCurrGame = gameName;
        this.nameOfPlayer = playerName;
        this.playerRetired = false;
    }
    
    public String getNameOfCurrentGamePlayed() {
        return nameOfCurrGame;
    }

    public void finishTurn() throws InvalidParameters_Exception {
        try {
            clientTurn = false;
            game.finishTurn(playerId);
        }
        catch (InvalidParameters_Exception ex) {
            throw ex;
        }
        finally {
            setTimer();
        }
    }
    
    public void setTimer() {
        if (!clientTurn) {
            if (timer != null && timerTask != null) {
                timer.cancel();
                timerTask.cancel(); 
            }
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (!clientTurn) {
                        handleEvents(true);
                    }
                }
            };
            timer.scheduleAtFixedRate(timerTask, 0, 12_000);
        } else {
            if (timer != null && timerTask != null) {
                timer.cancel();
                timerTask.cancel();
            }
            
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (clientTurn) {
                        checkIfPlayerNotResign();
                    }
                }
            };
            timer.scheduleAtFixedRate(timerTask, 0, 12_000);
        }
    }
    
    private void checkIfPlayerNotResign() {
        try {
            PlayerDetails playerDetails = game.getPlayerDetails(playerId);
            playerRetired = playerDetails.getStatus() == PlayerStatus.RETIRED;
        } catch (GameDoesNotExists_Exception | InvalidParameters_Exception ex) {
            playerRetired = true;
        }
    }
    
    private void handleEvents(boolean needToSleep) {
        Thread thread = new Thread (()-> {
            try { 
                List<Event> events = getEvents();
                doEvents(events, needToSleep);
            } catch (InvalidParameters_Exception ex) {
            }
        });
        thread.start();
    }

    private List<Event> getEvents() throws InvalidParameters_Exception {
        return game.getEvents(playerId, currentEventId + 1);
    }
    
    private void doEvents(List<Event> events, boolean needToSleep) {
        for (Event event : events) {
            if (event.getType() != EventType.GAME_START) {
                doEvent(event, event.equals(events.get(events.size() - 1)));
                currentEventId = event.getId();
                if (needToSleep)
                    sleep(); 
            }
        }
    }
    
    private void doEvent(Event event, boolean lastEvent) {
        String msg;
        boolean needToSetBoardCubePane = false;
        switch (event.getType()) {
            case GAME_WINNER: winner = event.getPlayerName();
                break;
            case PLAYER_TURN:
                checkValidationOfBoard();
                updateBackUpBoard(false);
                if (event.getPlayerName().equals(nameOfPlayer) && lastEvent) {
                    clientTurn = true;
                    setTimer();
                    messageForClient = "It's your turn. Please play!";
                }
                else {
                    messageForClient = "Current player: " + event.getPlayerName();
                    clientTurn = false;
                }
                break;
            case PLAYER_RESIGNED: messageForClient = "The player " + event.getPlayerName() + " has been resigned";
                if (event.getPlayerName().equals(nameOfPlayer)) {
                    playerRetired = true;
                }
                break;
            case PLAYER_FINISHED_TURN: messageForClient = "The player " + event.getPlayerName() + " finished his turn";
                if (event.getPlayerName().equals(nameOfPlayer)) {
                    clientTurn = false;
                }
                updateBackUpBoard(false);
                break;
            case SEQUENCE_CREATED: handleCreateSequenceEvent(event);
                needToSetBoardCubePane = true;
                break;
            case TILE_ADDED: handleTileAddedEvent(event);
                needToSetBoardCubePane = true;
                break;
            case REVERT: updateBackUpBoard(true);
                needToSetBoardCubePane = true;
                if (nameOfPlayer.equals(event.getPlayerName())) {
                    messageForClient = "You got punishment! Take 3 cubes from bank!";
                }
                break;
            case TILE_MOVED: handleTileMovedEvent(event);
                needToSetBoardCubePane = true;
                break;
            case TILE_RETURNED: handleTileReturnedEvent(event);
                needToSetBoardCubePane = true;
                break;
            case GAME_OVER: gameOver = true;
                break;
        }
    }
    
    private void handleCreateSequenceEvent(Event event) {
        if (event.getPlayerName() == null || !event.getPlayerName().equals(nameOfPlayer)) {
            ArrayList<Cube> cubes = (ArrayList<Cube>) Converter.convertListOfTilesToListOfCubes(event.getTiles());
            Cube joker = getJokerFromSerial(cubes);
            if (joker != null) {
                extensions.ExtensionMethods.convertJoker(joker, cubes);
            }
            
            extensions.ExtensionMethods.sortSerial(cubes);
            demoBoard.add(cubes);
        }
    }

    private void updateBackUpBoard(boolean revert) {
        if (!revert) {
            backUpBoard = demoBoard;
            copyTempListIntoNewOne();
        }
        else {
            copyTempListIntoNewOne();
        }
    }
    
    private void copyTempListIntoNewOne() {
        demoBoard = new ArrayList<ArrayList<Cube>>();
        for(ArrayList<Cube> serial : backUpBoard) {
            ArrayList<Cube> newSerial = new ArrayList<Cube> (serial);
            demoBoard.add(newSerial);
        }    
    }

    private void handleTileAddedEvent(Event event) {
        if (!event.getPlayerName().equals(nameOfPlayer)) {
            Cube cube = Converter.convertTileIntoCube(event.getTiles().get(0));
            ArrayList<Cube> serial = demoBoard.get(event.getTargetSequenceIndex() - 1);
            serial.add(event.getTargetSequencePosition(), cube);
            
            Cube joker = getJokerFromSerial(serial);
            if (joker != null) {
                extensions.ExtensionMethods.convertJoker(joker, serial);
            }
            
            extensions.ExtensionMethods.sortSerial(serial);
        }
    }

    private void handleTileMovedEvent(Event event) {
        if (!event.getPlayerName().equals(nameOfPlayer)) {
            Cube cube = Converter.convertTileIntoCube(event.getTiles().get(0));
            ArrayList<Cube> sourceSerial = demoBoard.get(event.getSourceSequenceIndex()- 1);
            Cube originalCube = sourceSerial.get(event.getSourceSequencePosition());
            
            if (originalCube.equals(cube)) {
                ArrayList<Cube> targetSerial = demoBoard.get(event.getTargetSequenceIndex() - 1);
                targetSerial.add(event.getTargetSequencePosition(), originalCube);
                sourceSerial.remove(originalCube);
                
                Cube joker = getJokerFromSerial(targetSerial);
                if (joker != null) {
                    extensions.ExtensionMethods.convertJoker(joker, targetSerial);
                }
                extensions.ExtensionMethods.sortSerial(targetSerial);
            }
        }
    }

    private void handleTileReturnedEvent(Event event) {
        if (!event.getPlayerName().equals(nameOfPlayer)) {
            Cube cube = Converter.convertTileIntoCube(event.getTiles().get(0));
            ArrayList<Cube> sourceSerial = demoBoard.get(event.getSourceSequenceIndex()- 1);
            Cube originalCube = sourceSerial.get(event.getSourceSequencePosition());
            
            if (originalCube.equals(cube)) {
                sourceSerial.remove(originalCube);
                if (sourceSerial.size() == 0) {
                    demoBoard.remove(sourceSerial);
                }
            }
        }
    }
    
    private void sendInsertActionToLogic(Action currAction) throws InvalidParameters_Exception {
        if (currAction.getBoardCube().size() > 0) {
            doMoveTileAction(currAction);
        }
        else {
            doAddTileAction(currAction);
        }
    }

    private void sendCreateSerialActionToLogic(Action currAction) throws InvalidParameters_Exception {
        List<Tile> tiles = Converter.convertListOfCubesToListOfTiles(currAction.getPlayerCubes());
        game.createSequence(playerId, tiles);
        
        Event newEvent = new Event();
        newEvent.setType(EventType.SEQUENCE_CREATED);
        newEvent.setPlayerName(nameOfPlayer + "123456");
        newEvent.getTiles().addAll(tiles);
        handleCreateSequenceEvent(newEvent);
        
        List<CubeBoard> cubesFromBoard = currAction.getBoardCube();
        
        for (CubeBoard cubeBoard : cubesFromBoard) {
            Action newAction = new Action();
            newAction.addBoardCube(cubeBoard);
            newAction.setActionType(ActionType.ADD_TO_BOTTOM_EDGE);
            newAction.setSerialNumber(demoBoard.size());
            doMoveTileAction(newAction);
        }
    }

    private void sendReplaceJokerAcionToLogic(Action currAction) throws InvalidParameters_Exception {
        CubeBoard jokerCube;
        
        if (currAction.getPlayerCubes().size() > 0) {
            Action tempAction = new Action();
            tempAction.setActionType(ActionType.ADD_TO_BOTTOM_EDGE);
            tempAction.setSerialNumber(currAction.getBoardCube().get(0).getNumOfSerial());
            tempAction.getPlayerCubes().add(currAction.getPlayerCubes().get(0));
            doAddTileAction(tempAction);
            
            jokerCube = currAction.getBoardCube().get(0);
            
        }
        else {
            CubeBoard boardCube;
            if(currAction.getBoardCube().get(0).getCube().isJoker()) {
                jokerCube = currAction.getBoardCube().get(0);
                boardCube = currAction.getBoardCube().get(1);
            }
            else {
                boardCube = currAction.getBoardCube().get(0);
                jokerCube = currAction.getBoardCube().get(1);
            }
            
            Action tempAction = new Action();
            tempAction.setActionType(ActionType.ADD_TO_BOTTOM_EDGE);
            tempAction.setSerialNumber(jokerCube.getNumOfSerial());
            tempAction.addBoardCube(boardCube);
            doMoveTileAction(tempAction);
        }
        
        Action tempAction = new Action();
        tempAction.setActionType(ActionType.ADD_TO_BOTTOM_EDGE);
        tempAction.setSerialNumber(demoBoard.size() + 1);
        tempAction.addBoardCube(jokerCube);
        doMoveTileAction(tempAction);
    }

    private void sendRegretActionToLogic(Action currAction) throws InvalidParameters_Exception {
        int sourceIndexSequnce = currAction.getBoardCube().get(0).getNumOfSerial();
        int sourceSequncePosition = getIndexOfCubeFromBoard(currAction.getBoardCube().get(0));
        game.takeBackTile(playerId, sourceIndexSequnce, sourceSequncePosition);
        
        Event newEvent = new Event();
        newEvent.setType(EventType.TILE_RETURNED);
        newEvent.setPlayerName(nameOfPlayer + "123456");
        newEvent.setSourceSequenceIndex(sourceIndexSequnce);
        newEvent.setSourceSequencePosition(sourceSequncePosition);
        newEvent.getTiles().add(Converter.convertCubeIntoTile(demoBoard.get(sourceIndexSequnce - 1).get(sourceSequncePosition)));
        handleTileReturnedEvent(newEvent);
    }

    private void doMoveTileAction(Action currAction) throws InvalidParameters_Exception {
        int sourceSequenceIndex, sourceSequencePosition, targetSequenceIndex, targetSequencePosition;
        
        sourceSequenceIndex = currAction.getBoardCube().get(0).getNumOfSerial();
        targetSequenceIndex = currAction.getSerialNumber();
        
        if (currAction.getActionType() == ActionType.ADD_TO_BOTTOM_EDGE) {
            targetSequencePosition = 0;
        }
        else if (currAction.getActionType() == ActionType.ADD_TO_TOP_EDGE) {
            targetSequencePosition = demoBoard.get(sourceSequenceIndex - 1).size() - 1;
        }
        else {
            targetSequencePosition = getIndexOfCubeWithSameData(currAction.getBoardCube().get(0).getCube(), targetSequenceIndex);
            if (targetSequencePosition == demoBoard.get(sourceSequenceIndex - 1).size()) {
                throw new InvalidParameters_Exception("You can't do split action in choosen sequence", null);
            }
        }
        
        sourceSequencePosition = getIndexOfCubeFromBoard(currAction.getBoardCube().get(0));
        
        game.moveTile(playerId, sourceSequenceIndex, sourceSequencePosition, targetSequenceIndex, targetSequencePosition);
    
        Event newEvent = new Event();
        newEvent.setType(EventType.TILE_MOVED);
        newEvent.setPlayerName(nameOfPlayer + "123456");
        newEvent.setSourceSequenceIndex(sourceSequenceIndex);
        newEvent.setSourceSequencePosition(sourceSequencePosition);
        newEvent.setTargetSequenceIndex(targetSequenceIndex);
        newEvent.setTargetSequencePosition(targetSequencePosition);
        newEvent.getTiles().add(Converter.convertCubeIntoTile(currAction.getBoardCube().get(0).getCube()));
        handleTileMovedEvent(newEvent);
        
        if (currAction.getActionType() == ActionType.SPLIT && targetSequencePosition != 0) {
            List<Cube> targetSerial = demoBoard.get(targetSequenceIndex - 1);
            demoBoard.add(new ArrayList<Cube>());
            int index = 0;
            for (int i = targetSequencePosition + 1; i < targetSerial.size(); i++) {
                newEvent = new Event();
                newEvent.setType(EventType.TILE_MOVED);
                newEvent.setPlayerName(nameOfPlayer + "123456");
                newEvent.setSourceSequenceIndex(targetSequenceIndex);
                newEvent.setSourceSequencePosition(i);
                newEvent.setTargetSequenceIndex(demoBoard.size() - 1);
                newEvent.setTargetSequencePosition(index);
                newEvent.getTiles().add(Converter.convertCubeIntoTile(targetSerial.get(i)));
                handleTileMovedEvent(newEvent);
            }
        }
    }

    private void doAddTileAction(Action currAction) throws InvalidParameters_Exception {
        int sequenceIndex, sequencePosition;
        Tile tile = Converter.convertCubeIntoTile(currAction.getPlayerCubes().get(0));
        sequenceIndex = currAction.getSerialNumber();
        
        if (currAction.getActionType() == ActionType.ADD_TO_BOTTOM_EDGE) {
            sequencePosition = 0;
        }
        else if (currAction.getActionType() == ActionType.ADD_TO_TOP_EDGE) {
            sequencePosition = demoBoard.get(sequenceIndex - 1).size();
        }
        else {
            sequencePosition = getIndexOfCubeWithSameData(currAction.getPlayerCubes().get(0), sequenceIndex);
        }
        
        game.addTile(playerId, tile, sequenceIndex, sequencePosition);
        
        Event newEvent = new Event();
        newEvent.setType(EventType.TILE_ADDED);
        newEvent.setPlayerName(nameOfPlayer + "123456");
        newEvent.setTargetSequenceIndex(sequenceIndex);
        newEvent.setTargetSequencePosition(sequencePosition);
        newEvent.getTiles().add(tile);
        handleTileAddedEvent(newEvent);
        
        if (currAction.getActionType() == ActionType.SPLIT && sequencePosition != 0) {
            List<Cube> targetSerial = demoBoard.get(sequenceIndex - 1);
            demoBoard.add(new ArrayList<Cube>());
            int index = 0;
            for (int i = sequencePosition + 1; i < targetSerial.size(); i++) {
                newEvent = new Event();
                newEvent.setType(EventType.TILE_MOVED);
                newEvent.setPlayerName(nameOfPlayer + "123456");
                newEvent.setSourceSequenceIndex(sequenceIndex);
                newEvent.setSourceSequencePosition(i);
                newEvent.setTargetSequenceIndex(demoBoard.size() - 1);
                newEvent.setTargetSequencePosition(index);
                newEvent.getTiles().add(Converter.convertCubeIntoTile(targetSerial.get(i)));
                handleTileMovedEvent(newEvent);
            }
        }
    }

    private int getIndexOfCubeFromBoard(CubeBoard cubeBoard) {
        int index = 0;
        ArrayList<Cube> serial = demoBoard.get(cubeBoard.getNumOfSerial() - 1);
        Cube cube = cubeBoard.getCube();
        
        for (Cube cubeFromSerial : serial) {
            if(cube.equals(cubeFromSerial)) {
                return index;
            }
            
            index++;
        }
        
        return index;
    }

    public void setClientTurn(boolean turn) {
        clientTurn = turn;
    }

        
    private Cube getJokerFromSerial(List<Cube> serial) {
        for (Cube cube : serial) {
            if (cube.isJoker()) {
                return cube;
            }
        }
        
        return null;
    }

    private int getIndexOfCubeWithSameData(Cube cubeToFind, int sequenceIndex) {
        int index = 0;
        List<Cube> serial = demoBoard.get(sequenceIndex - 1);
        for (Cube cube : serial) {
            if (cube.equals(cubeToFind)) {
                return index;
            }
            index++;
        }
        
        return 0;
    }
    
    public List<ArrayList<Cube>> getBoard() {
        return demoBoard;
    }
    
    public List<Cube> getPlayerCubesFromServer() {
        try {
            PlayerDetails playerDetails = game.getPlayerDetails(playerId);
            currentPlayerCanUseBoard = playerDetails.isPlayedFirstSequence();
            playersCubes = Converter.convertListOfTilesToListOfCubes(playerDetails.getTiles());
            return playersCubes;
        } catch (GameDoesNotExists_Exception ex) {
        } catch (InvalidParameters_Exception ex) {
        }
        return null;
    }
    
    public List<Cube> getPlayerCubes() {
        return playersCubes;
    }
    
    public List<PlayerDetails> getPlayersDetails(String gameName) throws GameDoesNotExists_Exception {
        return game.getPlayersDetails(gameName);
    }

    public String getMsg() {
        return messageForClient;
    }

    public boolean checkIfPlayerRetiredAndTheGameContinue() {
        try {
            GameDetails gameDetails = game.getGameDetails(nameOfCurrGame);
            return gameDetails.getStatus() == GameStatus.FINISHED && playerRetired;
        } catch (GameDoesNotExists_Exception ex) {
        }
        return playerRetired && !gameOver;
    }
    
    public void setDetailsWhenGameIsOver() {
        initialize();
    }

    private void checkValidationOfBoard() {
        if (!ExtensionMethods.isSerialsValid(demoBoard)) {
            currentEventId = -1;
            demoBoard.clear();
            handleEvents(false);
        }
    }
}