package rummikubLogic;

import exceptions.DuplicateNameException;
import exceptions.EmptyNameException;
import exceptions.TooManyPlayersAddedException;
import fileManager.EnumsForFile;
import fileManager.LoadSavedGame;
import generated.PlayerType;
import generated.Players;
import generated.Rummikub;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import dataObject.Cube;
import dataObject.CubeBoard;
import dataObject.Action;
import dataObject.ActionType;
import java.util.Timer;
import java.util.TimerTask;
import ws.rummikub.Event;
import ws.rummikub.EventType;
import ws.rummikub.InvalidParameters_Exception;

public class RummikubGameLogic {

    private static final int NUMBER_OF_CUBES_FOR_PLAYER = 14;
    private static final int CUBES_GIVEN_IN_PUNISHMENT = 3;
    private static final int POINTS_GIVEN_AS_PUNISHMENT_FOR_LEFT_JOKER = 30;
    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 4;

    private final Board board;
    private final List<Player> players;
    private final CubeBank cubeBank;
    private final List<Event> events;
    private int numberOfPlayers;
    private int indexNumberOfCurrentPlayer;
    private boolean gameOver;
    private boolean bankIsEmpty;
    private String nameOfGame;
    private Player currentPlayer;
    private boolean[] playersTookFromBank;
    private int winnerIndex;
    private int numOfPlayersJoined;
    private int numOfComputerPlayers;
    private int numOfHumanPlayers;
    private int numberOfActivePlayers;
    private boolean isLoadedFromXML;
    private GameStatus status;
    private Timer timer;
    private TimerTask timerTask;

    public RummikubGameLogic() {
        board = new Board();
        players = new ArrayList<Player>();
        events = new ArrayList<Event>();
        cubeBank = new CubeBank();
        numOfPlayersJoined = 0;
    }

    public void initiallizeNewGame(int numOfHumanPlayers, int numOfCompPlayers) {
        initComputerPlayers(numOfCompPlayers);
        this.numOfComputerPlayers = numOfCompPlayers;
        this.numOfHumanPlayers = numOfHumanPlayers;
        numberOfPlayers = numOfCompPlayers + numOfHumanPlayers;
        isLoadedFromXML = false;
        status = GameStatus.WAITING;
    }

    public void initiallizeNewRound() {
        Event event = new Event();
        event.setId(events.size());
        event.setType(EventType.GAME_START);
        events.add(event);

        if (!isLoadedFromXML) {
            board.clearBoard();
            cubeBank.initNewRound();
            initCubesForPlayers();
        }
        else {
            setEventsOfLoadedGame();
        }

        indexNumberOfCurrentPlayer = 0;
        gameOver = false;
        bankIsEmpty = false;

        playersTookFromBank = new boolean[numberOfPlayers];
        clearAllPlayersPassTheirTurn();
        status = GameStatus.ACTIVE;
        numberOfActivePlayers = numberOfPlayers;

        for (Player player : players) {
            player.setStatus(Player.PlayerStatus.ACTIVE);
        }

        setCurrentPlayer();
    }

    private void initCubesForPlayers() {

        for (Player player : players) {
            player.initNewRound();
            for (int i = 0; i < NUMBER_OF_CUBES_FOR_PLAYER; i++) {
                player.addCubeToPlayerWhileInitialize(cubeBank.getRandomCube());
            }
        }
    }

    private void initComputerPlayers(int numOfDigitalPlayers) {
        players.clear();
        String name;

        for (int i = 1; i <= numOfDigitalPlayers; i++) {
            name = "Computer" + i;

            Player player = new ComputerPlayer(name, true);

            players.add(player);
        }
    }

    private void initPlayers(ArrayList<Boolean> playersCanUseBoard) {
        int index = 0;

        for (Player player : players) {
            if (playersCanUseBoard != null) {
                player.setDoneWithFirstSerial(playersCanUseBoard.get(index));
            }

            index++;
        }
    }

    public boolean isNameOfPlayerIsUnique(String playersNameToCheck) {
        boolean isPlayerNameUnique = true;

        for (Player player : players) {
            if (player.getName().equals(playersNameToCheck)) {
                isPlayerNameUnique = false;
                break;
            }
        }

        return isPlayerNameUnique;
    }

    public List<Cube> getCubesFromPlayer() {
        return currentPlayer.getCubes();
    }

    public List<ArrayList<Cube>> getCubesFromBoard() {
        return board.getCubes();
    }

    public void setNextPlayer() {
        if (status == GameStatus.FINISHED) {
            return;
        }

        Event event = new Event();
        event.setId(events.size());
        event.setPlayerName(currentPlayer.getName());
        event.setType(EventType.PLAYER_FINISHED_TURN);
        events.add(event);

        do {
            indexNumberOfCurrentPlayer++;
            indexNumberOfCurrentPlayer = indexNumberOfCurrentPlayer % numberOfPlayers;
        } while (players.get(indexNumberOfCurrentPlayer).getStatus() == Player.PlayerStatus.RETIRED);

        setCurrentPlayer();
    }

    public boolean checkIfPlayerCanUseBoard() {
        return currentPlayer.isPlayerCanUseBoard();
    }

    public boolean doNewAction(Action currAction) {
        boolean validAction = false;

        switch (currAction.getActionType()) {
            case TAKE_FROM_BANK:
                validAction = doTakeFromBankAction();
                break;
            case CREATE_SERIAL:
                validAction = doCreateSerialAction(currAction);
                break;
            case SPLIT:
                validAction = doSplitAction(currAction);
                break;
            case ADD_TO_TOP_EDGE:
                validAction = doAddToEdgeAction(currAction);
                break;
            case ADD_TO_BOTTOM_EDGE:
                validAction = doAddToEdgeAction(currAction);
                break;
            case REPLACE_JOKER:
                validAction = doReplaceJokerAction(currAction);
                break;
        }

        if (currentPlayer.getNumerOfCubes() == 0) {
            gameOver = true;
            status = GameStatus.FINISHED;
            updateScore();
        }

        if (bankIsEmpty) {
            updateCurrentPlayerTakeFromBankAction(currAction.getActionType() == ActionType.TAKE_FROM_BANK);

            if (allPlayersPassTheirTurn()) {
                gameOver = true;
                status = GameStatus.FINISHED;
                updateScore();
            }

            if (currAction.getActionType() != ActionType.TAKE_FROM_BANK) {
                clearAllPlayersPassTheirTurn();
            }
        }

        currentPlayer.setPlayerPlayCurrentTurn(validAction && currAction.getActionType() != ActionType.TAKE_FROM_BANK);

        setTimer();

        return validAction;
    }

    private void updateCurrentPlayerTakeFromBankAction(boolean value) {
        playersTookFromBank[indexNumberOfCurrentPlayer] = value;
    }

    private boolean allPlayersPassTheirTurn() {
        int counter = 0;

        for (int i = 0; i < numberOfPlayers; i++) {
            if (playersTookFromBank[i]) {
                counter++;
            }
        }

        return counter == numberOfPlayers;
    }

    private void clearAllPlayersPassTheirTurn() {
        if (playersTookFromBank.length != numberOfPlayers) {
            playersTookFromBank = new boolean[numberOfPlayers];
        }

        for (int i = 0; i < numberOfPlayers; i++) {
            playersTookFromBank[i] = false;
        }
    }

    private boolean doTakeFromBankAction() {
        boolean validAction = true;

        if (cubeBank.getCubeSize() >= 1) {
            currentPlayer.addCubeToPlayerWhileRunningGame(cubeBank.getRandomCube(), true);
        } else {
            validAction = false;
        }

        if (isBankEmpty()) {
            bankIsEmpty = true;
        }

        return validAction;
    }

    private boolean doCreateSerialAction(Action currAction) {
        ArrayList serial = new ArrayList<Cube>();
        Cube currentCubeInSerial;
        boolean validAction = true;
        boolean serialHasJoker = false;
        Cube jokerCube = null;
        boolean jokerFromPlayer = false;

        for (Cube cube : currAction.getPlayerCubes()) {
            currentCubeInSerial = currentPlayer.getCubeWithSameData(cube);
            if (currentCubeInSerial != null) {
                currentPlayer.removeCubesFromPlayer(currentCubeInSerial);
                serial.add(currentCubeInSerial);

                if (currentCubeInSerial.isJoker()) {
                    serialHasJoker = true;
                    jokerFromPlayer = true;
                    jokerCube = currentCubeInSerial;
                }
            } else {
                validAction = false;
                return validAction;
            }
        }

        int indexOfCubeBoard = 0;
        ArrayList<CubeBoard> cubesFromBoard = currAction.getBoardCube();

        for (CubeBoard cube : cubesFromBoard) {
            currentCubeInSerial = board.getCubeWithSameData(cube, false);

            if (currentCubeInSerial != null) {
                serial.add(currentCubeInSerial);
                boolean removeSerial = board.removeCubeFromSerial(currentCubeInSerial, cube.getNumOfSerial());
                if (currentCubeInSerial.isJoker()) {
                    serialHasJoker = true;
                    jokerCube = currentCubeInSerial;
                }

                if (removeSerial) {
                    for (int i = indexOfCubeBoard + 1; i < cubesFromBoard.size(); i++) {
                        int numOfSerial = cubesFromBoard.get(i).getNumOfSerial();
                        if (numOfSerial > cube.getNumOfSerial()) {
                            cubesFromBoard.get(i).setNumOfSerial(numOfSerial - 1);
                        } else if (numOfSerial == cube.getNumOfSerial()) {
                            validAction = false;
                            return validAction;
                        }
                    }
                }
            } else {
                validAction = false;
                return validAction;

            }
            indexOfCubeBoard++;
        }

        if (serialHasJoker && jokerCube != null) {
            if (!ExtensionMethods.checkCurrentSerial(serial)) {
                extensions.ExtensionMethods.convertJoker(jokerCube, serial);
            }

            if (jokerFromPlayer) {
                currentPlayer.addValueToSumOfFirstSerial(jokerCube.getValue());
            }
        }

        ArrayList<Cube> newSerial = extensions.ExtensionMethods.sortSerial(serial);
        board.addSerialToBoard(newSerial);

        if (validAction) {
            Event event = new Event();
            event.setId(events.size());
            event.setPlayerName(currentPlayer.getName());
            event.setType(EventType.SEQUENCE_CREATED);
            event.getTiles().addAll(converter.Converter.convertListOfCubesToListOfTiles(newSerial));
            events.add(event);
        }

        return validAction;
    }

    private boolean doSplitAction(Action currAction) {
        boolean validMove = true;

        Cube cubeToInsert;
        boolean getCubeFromPlayer = true;

        if (currAction.getPlayerCubes().size() > 0) {
            cubeToInsert = currentPlayer.getCubeWithSameData(currAction.getPlayerCubes().get(0));
        } else {
            cubeToInsert = board.getCubeWithSameData(currAction.getBoardCube().get(0), true);
            getCubeFromPlayer = false;
        }

        if (cubeToInsert != null) {
            if (getCubeFromPlayer) {
                currentPlayer.removeCubesFromPlayer(cubeToInsert);
            } else {
                board.removeCubeFromSerial(cubeToInsert, currAction.getBoardCube().get(0).getNumOfSerial());
            }

            Cube cubeWithSameData = board.getCubeWithSameData(new CubeBoard(cubeToInsert, currAction.getSerialNumber()), true);
            if (cubeWithSameData != null) {
                board.insertCubeToEdgeOfSerial(cubeToInsert, board.getNumOfSerials(), false);
                board.merge2Serials(currAction.getSerialNumber(), board.getNumOfSerials());
            } else {
                board.insertCubeToEdgeOfSerial(cubeToInsert, currAction.getSerialNumber(), false);
            }
        } else {
            validMove = false;
        }

        if (validMove) {
            createInsertCubeEvent(currAction, getCubeFromPlayer, cubeToInsert);
        }

        return validMove;
    }

    private boolean doAddToEdgeAction(Action currAction) {
        boolean validAction = true;
        boolean insertToEnd = currAction.getActionType() == ActionType.ADD_TO_TOP_EDGE;

        Cube cubeToInsert;
        boolean getCubeFromPlayer = true;

        if (currAction.getPlayerCubes().size() > 0) {
            cubeToInsert = currentPlayer.getCubeWithSameData(currAction.getPlayerCubes().get(0));
        } else {
            cubeToInsert = board.getCubeWithSameData(currAction.getBoardCube().get(0), true);
            getCubeFromPlayer = false;
        }

        if (cubeToInsert != null) {
            if (getCubeFromPlayer) {
                currentPlayer.removeCubesFromPlayer(cubeToInsert);
            } else {
                board.removeCubeFromSerial(cubeToInsert, currAction.getBoardCube().get(0).getNumOfSerial());
            }

            board.insertCubeToEdgeOfSerial(cubeToInsert, currAction.getSerialNumber(), insertToEnd);
        } else {
            validAction = false;
        }

        if (validAction) {
            createInsertCubeEvent(currAction, getCubeFromPlayer, cubeToInsert);
        }

        return validAction;
    }

    private boolean doReplaceJokerAction(Action currAction) {
        boolean validAction = true;
        Cube alternativeCube = null;
        Cube cubeReplaced = null;

        if (currAction.getPlayerCubes().size() > 0) {
            Cube playerCube = currAction.getPlayerCubes().get(0);
            alternativeCube = currentPlayer.getCubeWithSameData(playerCube);
            currentPlayer.removeCubesFromPlayer(alternativeCube);
        } else {
            CubeBoard cubeBoard = getNonJokerCube(currAction.getBoardCube());
            if (cubeBoard != null) {
                alternativeCube = board.getCubeWithSameData(cubeBoard, true);
                board.removeCubeFromSerial(alternativeCube, cubeBoard.getNumOfSerial());
            }
        }

        if (alternativeCube != null) {
            int serialNumber = getJokerCube(currAction.getBoardCube()).getNumOfSerial();
            cubeReplaced = board.replaceJokerInCubeByLineSerialNumber(alternativeCube, serialNumber);
        }

        if (cubeReplaced == null || alternativeCube == null) {
            validAction = false;
        } else {
            // put joker on the board for the next action in your turn
            ArrayList<Cube> serialWithOnlyJoker = new ArrayList<Cube>();
            serialWithOnlyJoker.add(cubeReplaced);
            board.addSerialToBoard(serialWithOnlyJoker);
        }

        return validAction;
    }

    public boolean checkIfPlayerWantToTakeFromBank(Action currAction) {
        boolean isPlayerStillPlay = currAction.getActionType() == ActionType.TAKE_FROM_BANK;

        return isPlayerStillPlay;
    }

    public boolean checkValidation() {
        boolean validTurn = true;
        boolean cubesDroped = false;

        if (isCurrentPlayerPlayCurrentTurn()) {
            List<ArrayList<Cube>> boardSerials = board.getCubes();
            cubesDroped = currentPlayer.anyCubesDropedInTurn();
            if (cubesDroped) {
                sortAllSerials(boardSerials);
                validTurn = ExtensionMethods.isSerialsValid(boardSerials) && currentPlayer.isPlayerCanUseBoard();
            } else {
                validTurn = false;
            }
        }

        return validTurn;
    }

    public boolean isCurrentPlayerPlayCurrentTurn() {
        return currentPlayer.isPlayerPlayCurrentTurn();
    }

    public int getNumOfSerials() {
        return board.getNumOfSerials();
    }

    public boolean isCurrentPlayerIsHuman() {
        return !currentPlayer.isComputer();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean givePlayerPunishment() {
        Cube cube;
        int numOfCube = 0;
        boolean validAction = true;

        while (cubeBank.getCubeSize() > 0 && numOfCube < CUBES_GIVEN_IN_PUNISHMENT) {
            cube = cubeBank.getRandomCube();
            currentPlayer.addCubeToPlayerWhileRunningGame(cube, true);
            numOfCube++;
        }

        if (numOfCube < CUBES_GIVEN_IN_PUNISHMENT) {
            validAction = false;
        }

        if (isBankEmpty()) {
            bankIsEmpty = true;
        }

        return validAction;
    }

    public void updateValidMove(boolean validTurn) {
        currentPlayer.updateListOfCubes(validTurn);
        board.updateListOfCubes(validTurn);

        if (!validTurn) {
            Event event = new Event();
            event.setId(events.size());
            event.setPlayerName(currentPlayer.getName());
            event.setType(EventType.REVERT);
            events.add(event);
        }
    }

    public boolean isBankEmpty() {
        return cubeBank.getCubeSize() <= 0;
    }

    private void setCurrentPlayer() {
        currentPlayer = players.get(indexNumberOfCurrentPlayer);
        currentPlayer.setPlayerPlayCurrentTurn(false);

        Event event = new Event();
        event.setId(events.size());
        event.setPlayerName(currentPlayer.getName());
        event.setType(EventType.PLAYER_TURN);
        events.add(event);

        setTimer();

        doComputerActions();
    }

    private ArrayList<Action> getActionsFromComputerPlayer() {
        ComputerPlayer computer = (ComputerPlayer) currentPlayer;
        ArrayList<Action> computerActions = null;

        if (computer != null) {
            computerActions = computer.createValidActions(board.getCubes());
        }

        if (computerActions != null) {
            if (computerActions.size() == 1) {
                currentPlayer.setPlayerPlayCurrentTurn(computerActions.get(0).getActionType() == ActionType.TAKE_FROM_BANK);
            }
        }

        return computerActions;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    private void updateScore() {
        int indexOfRoundWinner;
        int[] scoresArr = new int[numberOfPlayers];

        initArrayOfScores(scoresArr);
        sumArrayOfScores(scoresArr);
        indexOfRoundWinner = findMinSumIndex(scoresArr);

        updateAllScoresAfterCalc(scoresArr, indexOfRoundWinner);

        Event winnerEvent = new Event();
        winnerEvent.setId(events.size());
        winnerEvent.setPlayerName(players.get(winnerIndex).getName());
        winnerEvent.setType(EventType.GAME_WINNER);
        events.add(winnerEvent);

        Event gameOverEvent = new Event();
        gameOverEvent.setId(events.size());
        gameOverEvent.setType(EventType.GAME_OVER);
        events.add(gameOverEvent);
        
        timerTask.cancel();
        timer.cancel();
    }

    public List<Score> getListOfPlayerSortedByPoints() {
        List<Score> playersScore = new ArrayList<Score>(players);

        Collections.sort(playersScore, players.get(0));

        return playersScore;
    }

    private void initArrayOfScores(int[] arr) {
        for (int i = 0; i < numberOfPlayers; i++) {
            arr[i] = 0;
        }
    }

    private void sumArrayOfScores(int[] arr) {
        for (int i = 0; i < numberOfPlayers; i++) {
            for (Cube cube : players.get(i).getCubes()) {
                arr[i] += cube.getValue() == 0 ? POINTS_GIVEN_AS_PUNISHMENT_FOR_LEFT_JOKER : cube.getValue();
            }
        }
    }

    private int findMinSumIndex(int[] arr) {
        int min = arr[0];
        int res = 0;

        for (int i = 1; i < numberOfPlayers; i++) {
            if (arr[i] < min) {
                min = arr[i];
                res = i;
            }
        }

        winnerIndex = res;

        return res;
    }

    private void updateAllScoresAfterCalc(int[] arr, int indexOfRoundWinner) {
        int sumOfRestPlayers = 0;

        for (int i = 0; i < numberOfPlayers; i++) {
            if (i != indexOfRoundWinner) {
                players.get(i).addAndRemovePlayerToScore(-1 * arr[i]);
                sumOfRestPlayers += arr[i];
            }
        }

        players.get(indexOfRoundWinner).addAndRemovePlayerToScore(sumOfRestPlayers);
    }

    public boolean isPlayerTryToTakeFromBank() {
        return playersTookFromBank[indexNumberOfCurrentPlayer];
    }

    public Score getWinner() {
        return players.get(winnerIndex);
    }

    public fileManager.EnumsForFile.FileLoadResults loadSavedGame(String path) {
        return LoadSavedGame.checkFileAndInitialize(path, this);
    }

    public void initGameFromLoadedFile(Rummikub rummikub) {
        nameOfGame = rummikub.getName();
        initPlayersFromLoadedGame(rummikub.getPlayers());
        numberOfPlayers = players.size();
        indexNumberOfCurrentPlayer = getIndexOfCurrentPlayerByName(rummikub.getCurrentPlayer());

        board.setBoardFromFile(getBoardSerialsFromFile(rummikub));
        setPlayersHandsFromFile(rummikub);

        initCubeBankFromFile();
        isLoadedFromXML = true;
        status = GameStatus.WAITING;
    }

    private List<ArrayList<Cube>> getBoardSerialsFromFile(Rummikub rummikub) {
        List<ArrayList<Cube>> serials = new ArrayList<ArrayList<Cube>>();
        ArrayList<Cube> serial;
        generated.Board gameBoard = rummikub.getBoard();
        Cube cube;

        serials = ExtensionMethods.replaceListOfTilesIntoListOfCubes(gameBoard.getSequence());

        return serials;
    }

    public boolean checkValidBoardFromLoadedFile(List<ArrayList<Cube>> logicBoardFromFileBoard) {
        boolean validTurn = true;

        sortAllSerials(logicBoardFromFileBoard);
        validTurn = ExtensionMethods.isSerialsValid(logicBoardFromFileBoard);

        return validTurn;
    }

    private int getIndexOfCurrentPlayerByName(String currentPlayer) {
        int index = 0;
        for (Player player : players) {
            if (player.getName().equals(currentPlayer)) {
                break;
            }
            index++;
        }

        return index;
    }

    private void setPlayersHandsFromFile(Rummikub rummikub) {
        for (Players.Player playerFromFile : rummikub.getPlayers().getPlayer()) {
            for (Player player : players) {
                if (player.getName() == playerFromFile.getName()) {
                    ArrayList<Cube> playerHand = ExtensionMethods.replaceTilesIntoCubes(playerFromFile.getTiles().getTile(), false);
                    player.setHandFromFile(playerHand);
                    break;
                }
            }
        }
    }

    private void initCubeBankFromFile() {
        ArrayList<Cube> allCubesInGame = new ArrayList<Cube>();
        allCubesInGame.addAll(board.getListOfAllCubes());
        allCubesInGame.addAll(getListOfAllCubesFromPlayers());
        cubeBank.initCubesFromLoadedGame(allCubesInGame);
    }

    private ArrayList<Cube> getListOfAllCubesFromPlayers() {
        ArrayList<Cube> allPlayersCubes = new ArrayList<Cube>();

        for (Player player : players) {
            allPlayersCubes.addAll(player.getCubes());
        }

        return allPlayersCubes;
    }

    public String getLastFilePath() {
        return fileManager.SaveGameManager.lastFilePath;
    }

    public EnumsForFile.FileSaveResults saveGameToFile() {
        return fileManager.SaveGameManager.saveGame(this);
    }

    public EnumsForFile.FileSaveResults saveGameToFile(String directoryForXmlFile, String xmlFileName) {
        return fileManager.SaveGameManager.saveAsGame(this, directoryForXmlFile, xmlFileName);
    }

    public EnumsForFile.FileSaveResults saveGameToFile(String xmlPath) {
        return fileManager.SaveGameManager.saveAsGame(this, xmlPath);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Board getGameBoard() {
        return board;
    }

    public String getGameName() {
        return nameOfGame;
    }

    private void sortAllSerials(List<ArrayList<Cube>> serials) {
        extensions.ExtensionMethods.sortAllSerials(serials);
    }

    public Player addPlayer(String name, boolean isHuman, boolean canUseBoard) {
        if (players.size() >= MAX_PLAYERS) {
            throw new TooManyPlayersAddedException(MAX_PLAYERS);
        }

        if (name.isEmpty()) {
            throw new EmptyNameException();
        }
        if (!isNameOfPlayerIsUnique(name)) {
            throw new DuplicateNameException();
        }

        Player player;

        if (isHuman) {
            player = new HumanPlayer(name, !isHuman);
            player.setStatus(Player.PlayerStatus.Wait);
        } else {
            player = new ComputerPlayer(name, !isHuman);
        }

        player.setDoneWithFirstSerial(canUseBoard);

        players.add(player);

        return player;
    }

    public void addHumanPlayer(String name, boolean canUseBoard, int playerID) throws InvalidParameters_Exception {
        Player newPlayer;

        try {
            if (!isLoadedFromXML) {
                newPlayer = addPlayer(name, true, canUseBoard);
            } else {
                newPlayer = getPlayerWithSameName(name);
                if (newPlayer == null) {
                    throw new InvalidParameters_Exception("The player is not participant in that game.", null);
                }
            }
        } catch (Exception ex) {
            throw new InvalidParameters_Exception("The player is already participant in that game.", null);
        }

        newPlayer.setID(playerID);
        newPlayer.setStatus(Player.PlayerStatus.JOINED);

        numOfPlayersJoined++;
        if (numOfPlayersJoined == numOfHumanPlayers) {
            initiallizeNewRound();
        }
    }

    public void setGameName(String gameName) {
        nameOfGame = gameName;
    }

    public int getMinAmountOfPlayers() {
        return MIN_PLAYERS;
    }

    public int getMaxAmountOfPlayers() {
        return MAX_PLAYERS;
    }

    public int getBankSize() {
        return cubeBank.getCubeSize();
    }

    public boolean isPlayerCanUseBoard() {
        return currentPlayer.canUseBoard;
    }

    public ArrayList<Cube> getBoardSerialByIndex(int serialIndex) {
        return board.getCubes().get(serialIndex);
    }

    public void removePlayerFromGame() {
        Event event = new Event();
        event.setId(events.size());
        event.setPlayerName(currentPlayer.getName());
        event.setType(EventType.PLAYER_RESIGNED);
        events.add(event);

        currentPlayer.setStatus(Player.PlayerStatus.RETIRED);

        numberOfActivePlayers--;
        numOfHumanPlayers--;

        if (numberOfActivePlayers < MIN_PLAYERS || numOfHumanPlayers == 0) {
            gameOver = true;
            status = GameStatus.FINISHED;
            updateScore();
        }

        if (bankIsEmpty) {
            playersTookFromBank[indexNumberOfCurrentPlayer] = true;
        } else {
            clearAllPlayersPassTheirTurn();
        }

        if (!gameOver) {
            setNextPlayer();
        }
    }

    public boolean checkIfValidNumberOfPlayers() {
        return players.size() >= MIN_PLAYERS && players.size() <= MAX_PLAYERS;
    }

    public boolean checkIfAllPlayersLeftAreDigital() {
        boolean res = true;

        for (Player player : players) {
            if (!player.isComputer()) {
                res = false;
                break;
            }
        }

        return res;
    }

    private CubeBoard getNonJokerCube(ArrayList<CubeBoard> boardCubes) {
        CubeBoard cube = null;

        for (CubeBoard cubeBoard : boardCubes) {
            if (!cubeBoard.getCube().isJoker()) {
                cube = cubeBoard;
                break;
            }
        }

        return cube;
    }

    private CubeBoard getJokerCube(ArrayList<CubeBoard> boardCubes) {
        CubeBoard cube = null;

        for (CubeBoard cubeBoard : boardCubes) {
            if (cubeBoard.getCube().isJoker()) {
                cube = cubeBoard;
                break;
            }
        }

        return cube;
    }

    public void cancelAction(int sequenceIndex, int sequencePosition) throws InvalidParameters_Exception {
        Cube cubeToTakeBack = board.getCubeFromSpecificLocation(sequenceIndex, sequencePosition);

        if (cubeToTakeBack == null) {
            throw new InvalidParameters_Exception("The position of the cube you inserted is invalid", null);
        }

        if (currentPlayer.isCubeInBackUpAndNotInCurrList(cubeToTakeBack)) {
            currentPlayer.addCubeToPlayerWhileRunningGame(cubeToTakeBack, false);
            board.removeCubeFromSerial(cubeToTakeBack, sequenceIndex);
            
            Event event = new Event();
            event.setId(events.size());
            event.setPlayerName(currentPlayer.getName());
            event.setType(EventType.TILE_RETURNED);
            event.getTiles().add(converter.Converter.convertCubeToTile(cubeToTakeBack));
            event.setSourceSequenceIndex(sequenceIndex);
            event.setSourceSequencePosition(sequencePosition);
            events.add(event);
        }
        setTimer();
    }

    private void initPlayersFromLoadedGame(Players players) {
        for (Players.Player player : players.getPlayer()) {
            addPlayer(player.getName(), player.getType() == PlayerType.HUMAN, player.isPlacedFirstSequence());

            if (player.getType() == PlayerType.HUMAN) {
                numOfHumanPlayers++;
            } else {
                numOfComputerPlayers++;
            }
        }

        numberOfPlayers = players.getPlayer().size();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RummikubGameLogic otherGame = (RummikubGameLogic) obj;

        if (this.nameOfGame.equals(otherGame.getGameName())) {
            return true;
        }

        return false;
    }

    public int getNumberOfComputerPlayers() {
        return numOfComputerPlayers;
    }

    public int getNumberOfHumanPlayers() {
        return numOfHumanPlayers;
    }

    public int getNumberOfPlayersJoinedToGame() {
        return numOfPlayersJoined;
    }

    public boolean isLoadedFromXML() {
        return isLoadedFromXML;
    }

    public GameStatus getGameStatus() {
        return status;
    }

    private Player getPlayerWithSameName(String name) {
        for (Player player : players) {
            if (!player.isComputer() && player.getName().equals(name)) {
                return player;
            }
        }

        return null;
    }

    public List<Event> getEventsByEventID(int eventId) throws InvalidParameters_Exception {
        List<Event> res = new ArrayList<Event>();

        if (eventId >= 0 && eventId <= events.size()) {
            for (int i = eventId; i < events.size(); i++) {
                res.add(events.get(i));
            }
        } else {
            throw new InvalidParameters_Exception("The eventID is out of range.", null);
        }

        return res;
    }

    private void checkIfNeedToTakeFromBank() {
        if (!currentPlayer.isPlayerPlayCurrentTurn() && !currentPlayer.isComputer()
                && currentPlayer.getStatus() != Player.PlayerStatus.RETIRED) {
            doTakeFromBankAction();
        }
    }
    
    private void doComputerActions() {
        if (currentPlayer.isComputer()) {
            ArrayList<Action> actions = getActionsFromComputerPlayer();

            for (Action action : actions) {
                doNewAction(action);
            }

            try {
                finishTurn();
            } catch (InvalidParameters_Exception ex) {
            }
        }
    }

    private void createInsertCubeEvent(Action currAction, boolean isGettingCubeFromPlayer, Cube cubeToInsert) {
        Event event = new Event();
        event.setId(events.size());
        event.setPlayerName(currentPlayer.getName());

        if (isGettingCubeFromPlayer) {
            event.setType(EventType.TILE_ADDED);
            int positionOfCubeInSerial = board.getIndexOfCubeInSerial(cubeToInsert, currAction.getSerialNumber());
            event.setTargetSequencePosition(positionOfCubeInSerial);
            event.setTargetSequenceIndex(currAction.getSerialNumber());
            event.getTiles().addAll(converter.Converter.convertListOfCubesToListOfTiles(currAction.getPlayerCubes()));
        } else {
            event.setType(EventType.TILE_MOVED);
            int positionToInsertCubeInSerial = board.getIndexOfCubeInSerial(cubeToInsert, currAction.getSerialNumber());
            event.setTargetSequencePosition(positionToInsertCubeInSerial);
            event.setTargetSequenceIndex(currAction.getSerialNumber());
            event.setSourceSequenceIndex(currAction.getBoardCube().get(0).getNumOfSerial());
            int positionOfCubeToInsert = board.getIndexOfCubeInSerial(currAction.getBoardCube().get(0).getCube(), currAction.getBoardCube().get(0).getNumOfSerial());
            event.setSourceSequencePosition(positionOfCubeToInsert);
            event.getTiles().add(converter.Converter.convertCubeToTile(currAction.getBoardCube().get(0).getCube()));
        }

        events.add(event);
    }

    public String getCurrentPlayerName() {
        return currentPlayer.getName();
    }

    public void setTimer() {
        if (!currentPlayer.isComputer()) {
            if (timer != null && timerTask != null) {
                timer.cancel();
                timerTask.cancel();
            }
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (!currentPlayer.isComputer()) {
                        removePlayerFromGame();
                    }
                }
            };
            timer.scheduleAtFixedRate(timerTask, 120_000, 120_000);
        } else {
            if (timer != null && timerTask != null) {
                timer.cancel();
                timerTask.cancel();
            }
        }
    }

    public void finishTurn() throws InvalidParameters_Exception {
        try {
            boolean validTurn = checkValidation();
            checkIfNeedToTakeFromBank();
            updateValidMove(validTurn);

            if (!validTurn) {
                givePlayerPunishment();
                throw new InvalidParameters_Exception("The Action is not valid, you get 3 cube for punishment", null);
            }
        }
        catch (InvalidParameters_Exception ex) {
            throw ex;
        }
        finally {
            boolean isGameOver = isGameOver();

            if (!isGameOver) {
                setNextPlayer();
            }
        }
    }

    private void setEventsOfLoadedGame() {
        List<ArrayList<Cube>> serials = board.getCubes();
        
        for (ArrayList<Cube> serial : serials) {
            Event event = new Event();
            event.setId(events.size());
            event.setType(EventType.SEQUENCE_CREATED);
            event.getTiles().addAll(converter.Converter.convertListOfCubesToListOfTiles(serial));
            events.add(event);
        }
    }

    public enum GameStatus {

        WAITING,
        ACTIVE,
        FINISHED;

        public String value() {
            return name();
        }

        public static GameStatus fromValue(String v) {
            return valueOf(v);
        }
    }
}
