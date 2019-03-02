package javaFx.Controllers;

import gameManager.GameManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import ws.rummikub.GameDoesNotExists_Exception;
import ws.rummikub.InvalidParameters_Exception;


public class SettingController implements Initializable {
    
    @FXML
    private TextField gameNameTextField;
    
    @FXML
    private TextField playerNameTextField;

    @FXML
    private Label errorMessageLabelForPlayer;
    
    @FXML
    private Button joinButton;
    
    @FXML
    private Button gameDetailsButton;
	
    @FXML
    private Button viewWaitingGamesButton;
    
    @FXML
    private Label gameNameLabel;

    @FXML
    private Label playerNameLabel;
    
    @FXML
    private Label errorMessageLabelForJoin;
    
    private boolean isErrorMessageShown = false;
    private boolean isGameNameInitialize = false;
    
    private GameManager gameManager;
    private Stage stage;

    public void setGame(GameManager gameManager) {
        this.gameManager = gameManager;
    }
    
    public void setStage(Stage stage){
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        playerNameTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                onPlayerNameChange();
            }
        });
    }

    protected void onPlayerNameChange() {
        updateAddPlayerButtonState();
        hideError(errorMessageLabelForPlayer);
    }
	
    @FXML
    protected void onViewWaitingGamesClicked(ActionEvent event){
        gameManager.setWaitingGamesScene();
    }
	
    @FXML
    protected void onJoinClicked(ActionEvent event){
        if (gameNameTextField.getText().isEmpty()) {
            errorMessageLabelForJoin.setText("You need to insert name of a game");
        }
        else if (playerNameTextField.getText().isEmpty()) {
            errorMessageLabelForJoin.setText("You need to insert name of a player");
        }
        else {
            errorMessageLabelForJoin.setText("");
            Thread thread = new Thread(() -> {
                try {
                    gameManager.joinGame(gameNameTextField.getText(), playerNameTextField.getText());
                    
                    Platform.runLater(() -> gameManager.setGameScene());
                } catch (GameDoesNotExists_Exception ex) {
                    Platform.runLater(() -> errorMessageLabelForJoin.setText("The game name insert don't exist in the server"));
                } catch (InvalidParameters_Exception ex) {
                    Platform.runLater(() -> errorMessageLabelForJoin.setText("The parameters for the game is invalid"));
                }
            });
            thread.start();
        }
    }
	
    @FXML
    protected void onGameDetailsClicked(ActionEvent event) {
        if (gameNameTextField.getText().equals("")) {
            showError("You need to insert name of a game first", errorMessageLabelForJoin);
        }
        else {
            gameManager.setGameDetailsScene();
        }
    }
    
    @FXML
    protected void onBackButtonClicked(ActionEvent event) {
        cleanScene();
        gameManager.setFirstScene();
    }
    
    private void updateAddPlayerButtonState() {
        boolean isEmptyName = getPlayerName().trim().isEmpty();
        boolean disable = isEmptyName || isErrorMessageShown;
    }
        
    private String getPlayerName() {
        return playerNameTextField.getText();
    }

    private void addPlayerToList(String name, boolean isHuman) {
        PlayerView playerView = new PlayerView(name, isHuman);
    }

    private void showPlayerError(String message, Label errorMsgLabel) {
        if (!isErrorMessageShown) {
            isErrorMessageShown = true;
            showError(message, errorMsgLabel);
        }
        updateAddPlayerButtonState();
    }
    
    private void showError(String message, Label errorMsgLabel) {
        errorMsgLabel.setText(message);
        FadeTransition animation = new FadeTransition();
        animation.setNode(errorMsgLabel);
        animation.setDuration(Duration.seconds(0.3));
        animation.setFromValue(0.0);
        animation.setToValue(1.0);
        animation.play();
    }
    
    public void showError(String message) {
        showError(message, errorMessageLabelForJoin);
    }

    private void hideError(Label errorMsgLabel) {
        if (isErrorMessageShown) {
            FadeTransition animation = new FadeTransition();
            animation.setNode(errorMsgLabel);
            animation.setDuration(Duration.seconds(0.3));
            animation.setFromValue(1.0);
            animation.setToValue(0.0);
            animation.play();

            isErrorMessageShown = false;
            errorMsgLabel.textProperty().setValue("");
            updateAddPlayerButtonState();
        }
    }

    public void cleanScene() {
        playerNameTextField.clear();
        playerNameTextField.setDisable(false);
        gameNameTextField.clear();
        gameNameTextField.setDisable(false);
        errorMessageLabelForPlayer.setText("");
        errorMessageLabelForJoin.setText("");
        isErrorMessageShown = false;
        isGameNameInitialize = false;
        joinButton.setDisable(false);
    }

    public String getGameName() {
        return gameNameTextField.getText();
    }
}
