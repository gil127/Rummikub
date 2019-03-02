package gameManager;

import fileManager.EnumsForFile;
import java.io.IOException;
import dataObject.Cube;
import java.util.ArrayList;
import java.util.List;
import javaFx.Controllers.GameOverController;
import javaFx.Controllers.GameSessionSceneController;
import javaFx.Controllers.OpenSceneController;
import javaFx.Controllers.SettingController;
import javaFx.CubeImageButton;
import javaFx.SceneBuilder;
import javafx.stage.Stage;
import dataObject.Action;
import dataObject.ActionType;
import dataObject.CubeBoard;
import ServerConfig.Server;
import converter.Converter;
import extensions.ExtensionMethods;
import fileManager.FileLoader;
import java.io.InputStream;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaFx.Controllers.ConnectToServerController;
import javaFx.Controllers.GameDetailsController;
import javaFx.Controllers.PlayerDetailsController;
import javaFx.Controllers.PlayersDetailsController;
import javaFx.Controllers.WaitingController;
import javaFx.Controllers.WaitingGamesController;
import javafx.application.Platform;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import ws.rummikub.Color;
import ws.rummikub.DuplicateGameName_Exception;
import ws.rummikub.Event;
import ws.rummikub.EventType;
import ws.rummikub.GameDetails;
import ws.rummikub.GameDoesNotExists_Exception;
import ws.rummikub.GameStatus;
import ws.rummikub.InvalidParameters_Exception;
import ws.rummikub.InvalidXML_Exception;
import ws.rummikub.PlayerDetails;
import ws.rummikub.RummikubWebService;
import ws.rummikub.RummikubWebServiceService;
import ws.rummikub.Tile;

public class GameManager {
    
    private final String CONNECT_TO_SERVER_SCENE_PATH = "Controllers/ConnectToServer.fxml";
    private final String OPEN_SCENE_PATH = "Controllers/OpenScene.fxml";
    private final String SETTING_SCENE_PATH = "Controllers/SettingScene.fxml";
    private final String GAME_SESSION_SCENE_PATH = "Controllers/GameSessionScene.fxml";
    private final String GAME_OVER_SCENE_PATH = "Controllers/GameOverScene.fxml";
    private final String WAITING_SCENE_PATH = "Controllers/WaitingScene.fxml";
    private final String GAMES_WAITING_PATH = "Controllers/WaitingGamesScene.fxml";
    private final String GAME_DETAILS_PATH = "Controllers/GameDetailsScene.fxml";
    private final String PLAYERS_DETAILS_PATH = "Controllers/PlayersDetailsScene.fxml";
    private final String PLAYER_DETAILS_PATH = "Controllers/PlayerDetailsScene.fxml";
    
    private RummikubWebServiceService service;
    private RummikubWebService game;
    private String lastLoadedGameName;
    private String serverIP;
    private String serverPort;
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
    
    public GameManager() {
        initialize();
    }
    
    private void initialize() {
        this.playerId = -1;
        this.currentEventId = 0;
        isGameOnWaiting = false;
        clientTurn = false;
        demoBoard = new ArrayList<ArrayList<Cube>>();
        backUpBoard = new ArrayList<ArrayList<Cube>>();
        currentPlayerCanUseBoard = false;
        gameOver = false;
        this.playerRetired = false;
    }
    
    public void run(Stage primaryStage) throws IOException {
        initScenes(primaryStage);
    }
    
    private void initScenes(Stage primaryStage) throws IOException {
        SceneBuilder.setPrimaryStage(primaryStage);
        SceneBuilder.setTitles();
        
        // 1
        ConnectToServerController connectToServerController = SceneBuilder.buildNewScene(CONNECT_TO_SERVER_SCENE_PATH);
        connectToServerController.setGame(this);
        connectToServerController.setStage(primaryStage);
        
        // 2
        OpenSceneController openSceneController = SceneBuilder.buildNewScene(OPEN_SCENE_PATH);
        openSceneController.setGame(this);
        openSceneController.setStage(primaryStage);
        
        // 3
        SettingController settingController = SceneBuilder.buildNewScene(SETTING_SCENE_PATH);
        settingController.setGame(this);
        settingController.setStage(primaryStage);
        
        // 4
        GameSessionSceneController gameController = SceneBuilder.buildNewScene(GAME_SESSION_SCENE_PATH);
        gameController.setGame(this);
        gameController.setStage(primaryStage);
        
        // 5
        GameOverController gameOverController = SceneBuilder.buildNewScene(GAME_OVER_SCENE_PATH);
        gameOverController.setGame(this);
        gameOverController.setStage(primaryStage);
        
        WaitingController waitingController = SceneBuilder.buildNewScene(WAITING_SCENE_PATH);
        waitingController.setGame(this);
        waitingController.setStage(primaryStage);
        
        // 6
        WaitingGamesController waitingGamesController = SceneBuilder.buildNewScene(GAMES_WAITING_PATH);
        waitingGamesController.setGame(this);
        waitingGamesController.setStage(primaryStage);
        
        //7
        GameDetailsController gameDetailsController = SceneBuilder.buildNewScene(GAME_DETAILS_PATH);
        gameDetailsController.setGame(this);
        gameDetailsController.setStage(primaryStage);
        
        //8
        PlayersDetailsController playersDetailsController = SceneBuilder.buildNewScene(PLAYERS_DETAILS_PATH);
        playersDetailsController.setGame(this);
        playersDetailsController.setStage(primaryStage);
        
        //9
        PlayerDetailsController playerDetailsController = SceneBuilder.buildNewScene(PLAYER_DETAILS_PATH);
        playerDetailsController.setGame(this);
        playerDetailsController.setStage(primaryStage);
        
        setFirstScene();
        SceneBuilder.showPrimary();
    }
    
    public void setFirstScene() {
        boolean succeedConnectToServer = false;
        if (isConfigFileExist()) {
            if (tryToConnectToTheServer()) {
                succeedConnectToServer = true;
            }
        }
        
        if (!succeedConnectToServer){
            SceneBuilder.loadSceneToStage(SceneBuilder.CONNECT_TO_SERVER_SCENE_INDEX);
        }
        else {
            SceneBuilder.loadSceneToStage(SceneBuilder.OPEN_SCENE_INDEX);
        }
    }
    
    public void setInitializeScene() {
        SettingController settingController = (SettingController) SceneBuilder.getSceneControler(SceneBuilder.SETTING_SCENE_INDEX);
        settingController.cleanScene();
        SceneBuilder.loadSceneToStage(SceneBuilder.SETTING_SCENE_INDEX);
        
    }
    
    public void startGame() {      
        GameSessionSceneController scene = (GameSessionSceneController) SceneBuilder.getSceneControler(SceneBuilder.GAME_SESSION_INDEX);
        scene.setScene();
        SceneBuilder.loadSceneToStage(SceneBuilder.GAME_SESSION_INDEX);
        setTimer();
    }
    
    public boolean checkIfGameHasValidNumOfPlayers() {
        //return theGame.checkIfValidNumberOfPlayers();
        return true;
    }
     
    public EnumsForFile.FileLoadResults loadGameFromXml(String path) {
        boolean notExitInput = true;
        boolean gamePlayed = false;
        EnumsForFile.FileLoadResults result;
        
        try {
            String gameName = game.createGameFromXML(path);
            result = EnumsForFile.FileLoadResults.FILE_VALID;
            lastLoadedGameName = gameName;
        }
        catch (DuplicateGameName_Exception | InvalidParameters_Exception | InvalidXML_Exception ex) {
            result = Converter.convertExceptionToEnumsOfFile(ex);
        }
        
        return result;
    }
    
    public String getNameOfLastGameCreated() {
        return lastLoadedGameName;
    }
    
    public List<ArrayList<CubeImageButton>> getImagesOfCubesFromBoard() {
        List<ArrayList<Cube>> cubesFromBoard = demoBoard;
        
        List<ArrayList<CubeImageButton>> imagesOfCubeSerial = new ArrayList<ArrayList<CubeImageButton>>();
        
        for(ArrayList<Cube> serial : cubesFromBoard) {
            imagesOfCubeSerial.add(getListOfImagesFromListOfCubes(serial));
        }
        
        return imagesOfCubeSerial;
    }
    
    public ArrayList<CubeImageButton> getImageOfCubeFromPlayer() {
        List<Cube> playersCubes = getCubesFromPlayer();

        return getListOfImagesFromListOfCubes(playersCubes);
    }
    
    private List<Cube> getCubesFromPlayer() {
        try {
            List<Cube> playersCubes = new ArrayList<Cube>();
            PlayerDetails playerDetails = game.getPlayerDetails(playerId);
            currentPlayerCanUseBoard = playerDetails.isPlayedFirstSequence();
            return Converter.convertListOfTilesToListOfCubes(playerDetails.getTiles());
        } catch (GameDoesNotExists_Exception ex) {
        } catch (InvalidParameters_Exception ex) {
        }
        return null;
    }
    
    private ArrayList<CubeImageButton> getListOfImagesFromListOfCubes(List<Cube> listOfCubes) {
        ArrayList<CubeImageButton> CubesImages = new ArrayList<CubeImageButton>();

        listOfCubes.stream().forEach((cube) -> {
                    CubesImages.add(getImageOfCube(cube));});
        
        return CubesImages;
    }
    
    private CubeImageButton getImageOfCube (Cube cubeToConvert) {
        String imageExtension = ".png";
        String imageDir = "/resources/Cube's images/";
        String fileName = null;
        
        if (!cubeToConvert.isJoker())  {
            imageDir += cubeToConvert.getColor().name() + "/";
            fileName = Integer.toString(cubeToConvert.getValue());
        }
        else {
            imageDir += "Joker/";
            fileName = "Joker";
        }
        
        return new CubeImageButton(cubeToConvert, ExtensionMethods.getImage(fileName, imageExtension, imageDir));
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

    public void finishGame() {
        Object sceneControler = SceneBuilder.getSceneControler(SceneBuilder.GAME_OVER_INDEX);
        if (sceneControler instanceof GameOverController) {
            if (playerRetired) {
                ((GameOverController)sceneControler).setWinnerNameToDisplay();
            }
            else {
                ((GameOverController)sceneControler).setPlayerRetired();
            }
        }
        initialize();
        SceneBuilder.loadSceneToStage(SceneBuilder.GAME_OVER_INDEX);
    }

    public boolean isCurrentPlayerTurn() {
        return clientTurn;
    }

    public ArrayList<Action> getComputerPlayerActions() {
        //return theGame.getActionsFromComputerPlayer();
        return null;
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

    public String getWinnerName() {
        return winner;
    }
    
    public void setNewGame() {
        SettingController settingScene = (SettingController) SceneBuilder.getSceneControler(SceneBuilder.SETTING_SCENE_INDEX);
        settingScene.cleanScene();
    }
    
    public void currentPlayerRetired() throws InvalidParameters_Exception{
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

    public void createNewGame(String gameName, int numberOfPlayers, int numberOfDigitalPlayers) throws DuplicateGameName_Exception, InvalidParameters_Exception {
        game.createGame(gameName,  numberOfPlayers - numberOfDigitalPlayers, numberOfDigitalPlayers);
    }

    public void setGameDetailsScene() {
        new Thread(() -> {
            SettingController settingController = (SettingController) SceneBuilder.getSceneControler(SceneBuilder.SETTING_SCENE_INDEX);
            try {
                String gameName = settingController.getGameName();
                GameDetails gameDetails = game.getGameDetails(gameName);
                Platform.runLater(() -> {
                    updateGameDetailsScene(gameDetails);
                });
            } catch (GameDoesNotExists_Exception ex) {
                Platform.runLater(() -> settingController.showError("The game you insert does not exist."));
            }
        }).start();

    }

    public void setWaitingGamesScene() {
        Thread thread = new Thread(() -> {
        List<String> waitingGames = new ArrayList<String>();        
        waitingGames = game.getWaitingGames();
        
        if (waitingGames.size() >= 1) {
            WaitingGamesController waitingGamesController = (WaitingGamesController) SceneBuilder.getSceneControler(SceneBuilder.WAITING_GAME_INDEX);
            waitingGamesController.setCurrentWaitingGames(waitingGames);
            
            Platform.runLater(() -> {
                waitingGamesController.setTable();
                SceneBuilder.loadSceneToStage(SceneBuilder.WAITING_GAME_INDEX);
            });
        }
        else {
            Platform.runLater(() -> {
                SettingController settingController = (SettingController) SceneBuilder.getSceneControler(SceneBuilder.SETTING_SCENE_INDEX);
                settingController.showError("There is no waiting games in the Server.");
            });
        }
        });
        
        thread.start();
    }

    private boolean isConfigFileExist() {
        try {
            Server serverConfig = FileLoader.loadServerConfigXMLFile("C:\\temp\\ServerConfigEx03.xml"); 
            serverIP = serverConfig.getIP();
            serverPort = serverConfig.getPort();
        }
        catch (Exception ex) {
            return false;
        }
        
        return true;
    }
    
    private boolean tryToConnectToTheServer() {
        try {
            URL url = new URL("http://" + serverIP + ":" + serverPort + "/RummikubGameEx04-LogicServer/RummikubWebServiceService");
            service = new RummikubWebServiceService(url);
            game = service.getRummikubWebServicePort();
        }
        catch (Exception ex) {
            return false;
        }
        
        return true;
    }
    
    public void configServer(String ip, String port) {
        serverIP = ip;
        serverPort = port;
        
        new Thread(new Runnable() {

            public void run() {
                if (tryToConnectToTheServer()) {
                    saveConfigFile();
                    Platform.runLater(()-> SceneBuilder.loadSceneToStage(SceneBuilder.OPEN_SCENE_INDEX));
                } else {
                    Platform.runLater(new Runnable() {

                        public void run() {
                            ConnectToServerController connectToServerController = (ConnectToServerController) SceneBuilder.getSceneControler(SceneBuilder.CONNECT_TO_SERVER_SCENE_INDEX);
                            connectToServerController.showError("The IP and the Port are invalid!");
                        }
                    });
                }
            }
        }).start();
    }

    private void saveConfigFile() {
        Server server = new Server();
        server.setIP(serverIP);
        server.setPort(serverPort);
        try {
            FileLoader.saveServerConfigXMLFile("C:\\temp\\ServerConfigEx03.xml", server);
        } 
        catch (Exception ex) {
        }
    }
    
    private void updateGameDetailsScene(GameDetails gameDetails) {
        GameDetailsController gameDetailsController = (GameDetailsController) SceneBuilder.getSceneControler(SceneBuilder.GAME_DETAILS_INDEX);
        gameDetailsController.setGameNameLabel(gameDetails.getName());
        gameDetailsController.setGameStatusLabel(gameDetails.getStatus().toString()); 
        gameDetailsController.setNumberOfPlayersLabel(String.valueOf(gameDetails.getHumanPlayers() + gameDetails.getComputerizedPlayers()));
        gameDetailsController.setNumberOfDigitalPlayersLabel(String.valueOf(gameDetails.getComputerizedPlayers()));
        gameDetailsController.setNumberOfHumanPlayersLabel(String.valueOf(gameDetails.getHumanPlayers()));
        gameDetailsController.setNumberOfHumanPlayersJoinedLabel(String.valueOf(gameDetails.getJoinedHumanPlayers()));
        SceneBuilder.loadSceneToStage(SceneBuilder.GAME_DETAILS_INDEX);
    }

    public void setGameScene() {
        new Thread( () -> {
        WaitingController waitingController = (WaitingController) SceneBuilder.getSceneControler(SceneBuilder.WAITING_INDEX);
        SettingController settingController = (SettingController) SceneBuilder.getSceneControler(SceneBuilder.SETTING_SCENE_INDEX);
        try {
            boolean gameStarted = false;
            this.currentEventId = 0;
            List<Event> events = getEvents();
            
            for (Event event : events) {
                if (event.getType() == EventType.GAME_START) {
                    this.currentEventId = event.getId();
                    gameStarted = true;
                    setNewGame();
                }
            }
            
            if (!gameStarted && !isGameOnWaiting) {
                isGameOnWaiting = true;
                Platform.runLater(() -> setWaitingScene());
            }
            else if (gameStarted) {
                isGameOnWaiting = false;
                waitingController.stopPolling();
                Platform.runLater(() -> startGame());
            }
        } catch (InvalidParameters_Exception ex) {
            if (isGameOnWaiting) {
                Platform.runLater(() -> waitingController.showError(ex.getMessage()));
            }
            else {
                Platform.runLater(() -> settingController.showError(ex.getMessage()));
            }
        }
        }).start();
    }

    public GameStatus getGameStatus(String gameName) throws GameDoesNotExists_Exception {
        return game.getGameDetails(gameName).getStatus();
    }

    public void setWaitingScene() {
        SceneBuilder.loadSceneToStage(SceneBuilder.WAITING_INDEX);
    }
    
    public void joinGame(String gameName, String playerName) throws GameDoesNotExists_Exception, InvalidParameters_Exception {
        this.playerId = game.joinGame(gameName, playerName);
        this.nameOfCurrGame = gameName;
        this.nameOfPlayer = playerName;
        this.playerRetired = false;
    }

    public void setPlayersDetailsScene(String gameName) throws GameDoesNotExists_Exception{
        PlayersDetailsController playersDetailsController = (PlayersDetailsController)SceneBuilder.getSceneControler(SceneBuilder.PLAYERS_DETAILS_INDEX);
        List<PlayerDetails> playersDetails = game.getPlayersDetails(gameName);
        
        Platform.runLater(() -> {
            playersDetailsController.setPlayersDetails(playersDetails);
            SceneBuilder.loadSceneToStage(SceneBuilder.PLAYERS_DETAILS_INDEX);
            });
    }

    public String getPlayerId() {
        return String.valueOf(playerId);
    }

    public void setPlayerDetailsScene() throws GameDoesNotExists_Exception, InvalidParameters_Exception {
        PlayerDetails playerDetails = game.getPlayerDetails(playerId);
        PlayerDetailsController playerDetailsController = (PlayerDetailsController)SceneBuilder.getSceneControler(SceneBuilder.PLAYER_DETAILS_INDEX);
        playerDetailsController.setSceneDetails(playerDetails);
        
        Platform.runLater( () -> {
          SceneBuilder.loadSceneToStage(SceneBuilder.PLAYER_DETAILS_INDEX);
        });
    }

    public String getNameOfCurrentGamePlayed() {
        return nameOfCurrGame;
    }

    public void finishTurn() throws InvalidParameters_Exception {
        try {
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
                        handleEvents();
                    }
                }
            };
            timer.scheduleAtFixedRate(timerTask, 0, 12_000);
        } else {
            if (timer != null && timerTask != null) {
                timer.cancel();
                timerTask.cancel();
            }
        }
    }
    
    private void handleEvents() {
        Thread thread = new Thread (()-> {
            try { 
                List<Event> events = getEvents();
                doEvents(events);
            } catch (InvalidParameters_Exception ex) {
            }
        });
        thread.start();
    }

    private List<Event> getEvents() throws InvalidParameters_Exception {
        return game.getEvents(playerId, currentEventId);
    }

    private void doEvents(List<Event> events) {
        for (Event event : events) {
            if (event.getType() != EventType.GAME_START) {
                doEvent(event, event.equals(events.get(events.size() - 1)));
                currentEventId = event.getId();
                sleep(); 
            }
        }
    }

    private void doEvent(Event event, boolean lastEvent) {
        GameSessionSceneController gameController = (GameSessionSceneController) SceneBuilder.getSceneControler(SceneBuilder.GAME_SESSION_INDEX);
        Platform.runLater(()-> gameController.showMessage("", Color.BLUE));
        String msg;
        boolean needToSetBoardCubePane = false;
        switch (event.getType()) {
            case GAME_WINNER: winner = event.getPlayerName();
                break;
            case PLAYER_TURN:  Platform.runLater(()->gameController.setCurrentPlayerName(event.getPlayerName()));
                updateBackUpBoard(false);
                if (event.getPlayerName().equals(nameOfPlayer) && lastEvent) {
                    clientTurn = true;
                    setTimer();
                    playSound();
                    Platform.runLater(() -> gameController.setScene());
                }
                else {
                    clientTurn = false;
                }
                break;
            case PLAYER_RESIGNED: msg = "The player " + event.getPlayerName() + " has been resigned";
                Platform.runLater(()-> gameController.showMessage(msg, Color.BLUE));
                if (event.getPlayerName().equals(nameOfPlayer)) {
                    playerRetired = true;
                    Platform.runLater( () -> finishGame());
                }
                break;
            case PLAYER_FINISHED_TURN: msg = "The player " + event.getPlayerName() + " finished his turn";
                if (event.getPlayerName().equals(nameOfPlayer)) {
                    clientTurn = false;
                }
                
                updateBackUpBoard(false);
                Platform.runLater(()-> gameController.showMessage(msg, Color.BLUE));
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
                    Platform.runLater(()-> gameController.showMessage("You got punishment! Take 3 cubes from bank!", Color.RED));
                }
                break;
            case TILE_MOVED: handleTileMovedEvent(event);
                needToSetBoardCubePane = true;
                break;
            case TILE_RETURNED: handleTileReturnedEvent(event);
                needToSetBoardCubePane = true;
                break;
            case GAME_OVER: Platform.runLater(() ->finishGame());
                break;
        }
        
        if (needToSetBoardCubePane) {
            Platform.runLater(() -> gameController.setBoardCubesPane(clientTurn));
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

    private void playSound() {
        try {
            Clip clip = AudioSystem.getClip();
            InputStream resourceAsStream = GameManager.class.getResourceAsStream("/resources/MenuMove.wav");
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(resourceAsStream);
            clip.open(inputStream);
            clip.start(); 
    } catch (Exception e) {
        e.printStackTrace();
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
        
        jokerCube.setNumOfSerial(jokerCube.getNumOfSerial() + 1);
        
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
}