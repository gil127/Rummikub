package javaFx.Controllers;

import gameManager.GameManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javaFx.CubeImageButton;
import javaFx.CustomizablePromptDialog;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import dataObject.Action;
import dataObject.ActionType;
import dataObject.CubeBoard;
import ws.rummikub.Color;
import ws.rummikub.GameDoesNotExists_Exception;
import ws.rummikub.InvalidParameters_Exception;

public class GameSessionSceneController implements Initializable{
    private final int SEPARATOR_WIDTH = 30;
    private final int CUBE_BUTTON_WIDTH = 45;
    
    @FXML
    private FlowPane playerCubesPane;
    
    @FXML
    private FlowPane boardCubesPane;
    
    @FXML
    private Button finishTurnButton;
    
    @FXML
    private Button exitButton;
    
    @FXML
    private Label nameLabel;
    
    @FXML
    private Label messageLabel;
    
    @FXML
    private Label currentPlayerLabel;
    
    @FXML
    private Label currentPlayerNameLabel;
    
    @FXML
    private Button createSerialButton;
    
    @FXML
    private Button insertToSerialButton;
    
    @FXML
    private Button replaceJokerButton;
    
    @FXML
    private FlowPane currentActionPane;

    @FXML
    private Button finishActionButton;
    
    @FXML
    private ComboBox insertOptionComboBox;
    
    @FXML
    private ComboBox serialNumberToInsertComboBox;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private Label guidanceLabel;
    
    @FXML
    private Button setSerialNumberButton;
    
    @FXML
    private Button takeBackTileButton;
    
    private Stage stage;
    private GameManager gameManager;
    private boolean isCurrentPlayerPlayed = false;
    private Action currentActionOfPlayer = null;
    
    public void setGame(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        insertOptionComboBox.setItems(FXCollections.observableArrayList("Insert to the end", "Insert to the begining", "Insert to the middle"));
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }
    
    @FXML
    private void exitButtonClicked(ActionEvent event) {
        String result = CustomizablePromptDialog.show(stage, "Are you sure you want to exit?", "Yes", "No");
        
        if (result.equals("Yes")) {
            Thread thread = new Thread(()-> {
            try {
                gameManager.currentPlayerRetired();
                Platform.runLater(() -> gameManager.finishGame());
            } catch (InvalidParameters_Exception ex) {
                Platform.runLater(() -> {
                    messageLabel.setText(ex.getMessage());
                    messageLabel.setTextFill(Paint.valueOf("RED"));
                });
            }});
            thread.start();
        }
    }
    
    @FXML
    private void finishTurnClicked(ActionEvent event) {
       finishCurrentTurn();
    }
    
    public void setCurrentPlayerName(String currentPlayer) {
        nameLabel.setText(currentPlayer);
    }
    
    public void setScene() {
        setPanes();
        disableButtonsWhenFinishAction();
    }

    private void setPlayerCubesPane(boolean isCurrentPlayerTurn) {
        ArrayList<CubeImageButton> currentPlayerCubeButtons = gameManager.getImageOfCubeFromPlayer();
        
        setSettingForSerialOfCubeButton(currentPlayerCubeButtons, isCurrentPlayerTurn);
        
        playerCubesPane.getChildren().addAll(currentPlayerCubeButtons);
    }
    
    private void setSettingForSerialOfCubeButton(ArrayList<CubeImageButton> serialOfCubeButtons, boolean isCurrentPlayerTurn) {
        for(CubeImageButton cubeButton : serialOfCubeButtons) {
            cubeButton.setOnMouseClicked(this :: cubeButtonClicked);
            setDisableOrEnableForCubeButton(cubeButton, isCurrentPlayerTurn, false);
        }
    }
    
    private void setDisableOrEnableForCubeButton(CubeImageButton cubeButton, boolean isCurrentPlayerTurn, boolean onPlayerAction) {
        boolean enableButton = isCurrentPlayerTurn && onPlayerAction;
        cubeButton.setDisable(!enableButton);
    }
    
    private void cubeButtonClicked(MouseEvent event) {
        CubeImageButton cubeButtonClicked = getCubeButtonClicked(event);
        if (cubeButtonClicked != null){
            doActionWhenCubeButtonClicked(cubeButtonClicked);
            finishActionButton.setDisable(false);
        }
    }

    public void setBoardCubesPane(boolean isClientTurn) {
        boardCubesPane.getChildren().clear();
        List<ArrayList<CubeImageButton>> boardCubeImages = gameManager.getImagesOfCubesFromBoard();
        List<CubeImageButton> cubes;
        int currentPosXOfLastCube = 0;
        int currSerialWidth = 0;
        boolean firstSerial = true;
        int separatorWidth;
        double paneWidth = boardCubesPane.getPrefWidth();
        int count = 1;
        int labelWidth = 5;
        
        for(ArrayList<CubeImageButton> serialOfImages : boardCubeImages) {
            setSettingForSerialOfCubeButton(serialOfImages, isClientTurn);
            currSerialWidth = CUBE_BUTTON_WIDTH * serialOfImages.size() + SEPARATOR_WIDTH + labelWidth;
            
            if (paneWidth < currentPosXOfLastCube + currSerialWidth) {
                separatorWidth = (int)paneWidth - currentPosXOfLastCube;
                currentPosXOfLastCube = currSerialWidth - SEPARATOR_WIDTH;
            }
            else {
                separatorWidth = SEPARATOR_WIDTH;
                currentPosXOfLastCube += currSerialWidth;
            }

            if (!firstSerial) {
                Separator sep = new Separator(Orientation.VERTICAL);
                sep.setPrefWidth(separatorWidth);
                boardCubesPane.getChildren().add(sep);
            }
            
            boardCubesPane.getChildren().add(new Label(Integer.toString(count)));
            boardCubesPane.getChildren().addAll(serialOfImages);
            
            count++;
            firstSerial = false;
        }
    }
    
    private void clearAllPanes() {
        playerCubesPane.getChildren().clear();
        boardCubesPane.getChildren().clear();
        currentActionPane.getChildren().clear();
    }

    private void setPanes() {
        boolean isPlayerTurn = gameManager.isCurrentPlayerTurn();
        messageLabel.setText("");
        String guidMsg = isPlayerTurn? "Select action" : "";
        guidanceLabel.setText(guidMsg);
        clearAllPanes();
        setPlayerCubesPane(isPlayerTurn);
        setBoardCubesPane(isPlayerTurn);
    }

   private void finishCurrentTurn() {
        isCurrentPlayerPlayed = false;
        gameManager.setClientTurn(false);
        
        new Thread(()-> {
            try {
                gameManager.finishTurn();
            } catch (InvalidParameters_Exception ex) {
                Platform.runLater(() -> showMessage(ex.getMessage(), Color.RED));
            }
            finally {
                Platform.runLater(() -> setScene());
            }
        }).start();
    }

    private void disableButtonsWhenFinishAction() {
        boolean isCurrentPlayerTurn = gameManager.isCurrentPlayerTurn();
        boolean isPlayerCanUseBoard = gameManager.isPlayerCanUseBoard();
        finishTurnButton.setDisable(!isCurrentPlayerTurn);
        exitButton.setDisable(!isCurrentPlayerTurn || isCurrentPlayerPlayed);
        createSerialButton.setDisable(!isCurrentPlayerTurn);
        takeBackTileButton.setDisable(!(isCurrentPlayerTurn && isCurrentPlayerPlayed));
        insertToSerialButton.setDisable(!isCurrentPlayerTurn || !isPlayerCanUseBoard);
        replaceJokerButton.setDisable(!isCurrentPlayerTurn || !isPlayerCanUseBoard);
        finishActionButton.setDisable(true);
        cancelButton.setDisable(true);
        disableInsertButtons();
    }

    @FXML
    private void createSerialClicked(ActionEvent event) {        
        guidanceLabel.setText("choose the cubes for your serial");
        setNewAction(ActionType.CREATE_SERIAL);
    }
    
    @FXML
    private void insertToSerialClicked(ActionEvent event) {
        messageLabel.setText("");
        guidanceLabel.setText("choose the serial number and where do you want to insert");
        setSerialNumberButton.setDisable(false);
        setSerialNumberButton.setOpacity(1);
        insertOptionComboBox.setDisable(false);
        insertOptionComboBox.setOpacity(1);
        insertOptionComboBox.setValue(insertOptionComboBox.getPromptText());
        serialNumberToInsertComboBox.setDisable(false);
        serialNumberToInsertComboBox.setOpacity(1);
        serialNumberToInsertComboBox.setValue(serialNumberToInsertComboBox.getPromptText());
        serialNumberToInsertComboBox.setItems(FXCollections.observableArrayList(getListOfNumberOfSerial()));
        cancelButton.setDisable(false);
        disableButtonInAction(true);
    }
    
    @FXML
    private void replaceJokerClicked(ActionEvent event) {
        guidanceLabel.setText("choose the cube and the joker");
        setNewAction(ActionType.REPLACE_JOKER);
    }
    
    public void setNewAction(ActionType actionType) {
        messageLabel.setText("");
        currentActionOfPlayer = new Action();
        currentActionOfPlayer.setActionType(actionType);
        
        disableButtonInAction(false);
    }

    @FXML
    private void cancelClicked(ActionEvent event) {
        setPanes();
        disableButtonsWhenFinishAction();
        guidanceLabel.setText("Select action");
    }
    
    @FXML
    private void finishActionClicked(ActionEvent event) {
        doAction(false);
    }

    @FXML
    private void onPlayersDetailsClicked(ActionEvent event){
        try {
            gameManager.setPlayersDetailsScene(gameManager.getNameOfCurrentGamePlayed());
        } catch (GameDoesNotExists_Exception ex) {
            messageLabel.setText(ex.getMessage());
            messageLabel.setTextFill(Paint.valueOf("RED"));
        }
    }
    
    private ActionType getActionTypeValue() {
        
        String str = (String)insertOptionComboBox.getValue();
        ActionType res;
        
        switch(str) {
            case "Insert to the end":
                res = ActionType.ADD_TO_TOP_EDGE;
                break;
            case "Insert to the begining" :
                res = ActionType.ADD_TO_BOTTOM_EDGE;
                break;
            case "Insert to the middle" :
                res = ActionType.SPLIT;
                break;
            default:
                res = null;
                break;
        }
        
        return res;
    }

    private boolean isCubeFromPlayerPane(CubeImageButton cubeClicked) {
        for (Node node : playerCubesPane.getChildren()) {
            if (node instanceof CubeImageButton) {
                if (cubeClicked.getLayoutX() == node.getLayoutX() && cubeClicked.getLayoutY() == node.getLayoutY()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean isCubeFromBoardPane(CubeImageButton cubeClicked) {
        for (Node node : boardCubesPane.getChildren()) {
            if (node instanceof CubeImageButton) {
                if (cubeClicked.getLayoutX() == node.getLayoutX() && cubeClicked.getLayoutY() == node.getLayoutY()) {
                    return true;
                }
            }
        }
        return false;
    }

    private CubeImageButton getCubeButtonClicked(MouseEvent event) {
        CubeImageButton cubeButton = null;
        Node nodeClicked = event.getPickResult().getIntersectedNode();
        if (nodeClicked instanceof CubeImageButton) {
            cubeButton = (CubeImageButton) nodeClicked;
        }
        
        return cubeButton;
    }

    private void doActionWhenCubeButtonClicked(CubeImageButton cubeButtonClicked) {        
        if (isCubeFromPlayerPane(cubeButtonClicked)) {
            currentActionOfPlayer.addPlayerCubes(cubeButtonClicked.getCube());
            playerCubesPane.getChildren().remove(cubeButtonClicked);
            if (currentActionOfPlayer.getActionType() == ActionType.REPLACE_JOKER) {
                setDisableOrEnableForCubeButtonsInScene(playerCubesPane.getChildren(), true, false);
            }
        }
        else if (isCubeFromBoardPane(cubeButtonClicked)) {
            int serialNumber = getSerialNumberOfCube(cubeButtonClicked);
            CubeBoard cubeBoard = new CubeBoard(cubeButtonClicked.getCube(), serialNumber);
            currentActionOfPlayer.addBoardCube(cubeBoard);
            boardCubesPane.getChildren().remove(cubeButtonClicked);
            if (currentActionOfPlayer.getActionType() == ActionType.REPLACE_JOKER && 
                    currentActionOfPlayer.getBoardCube().size() + currentActionOfPlayer.getPlayerCubes().size() > 1) {
                setDisableOrEnableForCubeButtonsInScene(boardCubesPane.getChildren(), true, false);
                setDisableOrEnableForCubeButtonsInScene(playerCubesPane.getChildren(), true, false);
            }
            else if (currentActionOfPlayer.getActionType() == ActionType.TAKE_TILE_BACK) {
                setDisableOrEnableForCubeButtonsInScene(boardCubesPane.getChildren(), true, false);
            }
        }
        
        if (insertActionChoosen()) {
            setDisableOrEnableForCubeButtonsInScene(boardCubesPane.getChildren(), true, false);
            setDisableOrEnableForCubeButtonsInScene(playerCubesPane.getChildren(), true, false);
        }
        
        cubeButtonClicked.setDisable(true);
        currentActionPane.getChildren().add(cubeButtonClicked);
    }

    private void setDisableOrEnableForCubeButtonsInScene(ObservableList<Node> nodes, boolean isCurrentPlayerHuman, boolean isOnAction) {
        for (Node node : nodes) {
            if (node instanceof CubeImageButton) {
                setDisableOrEnableForCubeButton((CubeImageButton)node, isCurrentPlayerHuman, isOnAction);
            }
        }
    }

    private int getSerialNumberOfCube(CubeImageButton cubeButtonClicked) {
        int numOfSerial = 1;
        
        for (Node node : boardCubesPane.getChildren()) {
            if (node instanceof Separator) {
                numOfSerial++;
            }
            else if (node.equals(cubeButtonClicked)) {
                break;
            }
        }
        
        return numOfSerial;
    }

    private void disableButtonInAction(boolean insertAction) {
        cancelButton.setDisable(false);
        createSerialButton.setDisable(true);
        insertToSerialButton.setDisable(true);
        replaceJokerButton.setDisable(true);
        finishTurnButton.setDisable(true);
        takeBackTileButton.setDisable(true);
        exitButton.setDisable(true);
        if (!insertAction) {
            disableInsertButtons();
            setDisableOrEnableForCubeButtonsInScene(playerCubesPane.getChildren(), true, true);
            setDisableOrEnableForCubeButtonsInScene(boardCubesPane.getChildren(), gameManager.isPlayerCanUseBoard(), true);
        }
    }

    private void doAction(boolean isLastAction) {
        try {
            boolean actionWith2Jokers = gameManager.checkIfActionWith2Jokers(currentActionOfPlayer);
            if (actionWith2Jokers) {
                messageLabel.setText("You can't do action with 2 Jokers.");
                messageLabel.setTextFill(Paint.valueOf("RED"));
                cancelClicked(null);
            } 
            else {
                Thread thread = new Thread(() -> {
                try {
                    gameManager.sendActionToLogic(currentActionOfPlayer);
                    Platform.runLater(() -> setPanes());
                    isCurrentPlayerPlayed = true;
                    Platform.runLater(() -> disableButtonsWhenFinishAction());
                    resetCurrentAction();
                }
                catch (InvalidParameters_Exception ex) {
                    Platform.runLater(() -> showMessage(ex.getMessage(), Color.RED));
                }
                });
                thread.start();

                guidanceLabel.setText("");
            }
        } 
        catch (Exception ex) {
            messageLabel.setText("The action is not valid.");
            messageLabel.setTextFill(Paint.valueOf("RED"));
            cancelClicked(null);
        }
    }

    @FXML
    private void setPositionToInsertClicked(ActionEvent event) {
        Integer serialNumber = -1;
        
        try {
            serialNumber = (int)serialNumberToInsertComboBox.getValue();
        }
        catch (Exception ex) {
            messageLabel.setText("Please choose serial number to insert.");
            messageLabel.setTextFill(Paint.valueOf("RED"));
        }
        
        if (serialNumber != -1) {
            try {
                ActionType actionType = getActionTypeValue();
                if (actionType != null) {
                    setNewAction(actionType);
                    currentActionOfPlayer.setSerialNumber(serialNumber);
                    guidanceLabel.setText("Select Cube :");
                } 
                else {
                    messageLabel.setText("Please choose where do you want to insert the cube in the choosen serial.");
                    messageLabel.setTextFill(Paint.valueOf("RED"));
                }
            } 
            catch (Exception ex) {
                messageLabel.setText("Please choose where do you want to insert the cube in the choosen serial.");
                messageLabel.setTextFill(Paint.valueOf("RED"));
            }
        }
    }

    private boolean insertActionChoosen() {
        ActionType actionType = currentActionOfPlayer.getActionType();
        return actionType == ActionType.ADD_TO_BOTTOM_EDGE ||
                actionType == ActionType.ADD_TO_TOP_EDGE ||
                actionType == ActionType.SPLIT;
    }
    
    private ArrayList<Integer> getListOfNumberOfSerial() {
        int numOfSerials = gameManager.getNumOfSerials();
        ArrayList<Integer> list = new ArrayList<Integer>();

        for (int i = 1 ; i <= numOfSerials; i++) {
            list.add(i);
        }
        
        return list;
    }

    private void disableInsertButtons() {
        insertOptionComboBox.setDisable(true);
        insertOptionComboBox.setOpacity(0);
        serialNumberToInsertComboBox.setDisable(true);
        serialNumberToInsertComboBox.setOpacity(0);
        setSerialNumberButton.setDisable(true);
        setSerialNumberButton.setOpacity(0);
    }

    private void resetCurrentAction() {
        currentActionOfPlayer.setActionType(ActionType.REGRET_ACTION);
        currentActionOfPlayer.setSerialNumber(0);
        currentActionOfPlayer.getPlayerCubes().clear();
        currentActionOfPlayer.getBoardCube().clear();
    }
    
    public void showMessage(String msg, Color color) {
        messageLabel.setText(msg);
        messageLabel.setTextFill(Paint.valueOf(color.name()));
    }
    
    @FXML
    protected void onTakeBackTileClicked(ActionEvent event) {
        guidanceLabel.setText("Choose cube from board to take back to your hand");
        setNewAction(ActionType.TAKE_TILE_BACK);
        setDisableOrEnableForCubeButtonsInScene(playerCubesPane.getChildren(), true, false);
    }
}