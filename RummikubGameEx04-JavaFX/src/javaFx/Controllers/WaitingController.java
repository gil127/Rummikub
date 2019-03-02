package javaFx.Controllers;

import gameManager.GameManager;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;


public class WaitingController implements Initializable{
    private GameManager gameManager;
    private Stage stage;
    private Timer timer;
    
    @FXML
    private Label errorLabel;
    
    public WaitingController() {
        timer = new Timer();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
    public void startPollingEvents() {
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                gameManager.setGameScene();
            }
        };
        
        timer.schedule(timerTask, 0, 2_000);
    }
    
    public void stopPolling() {
        timer.cancel();
    }

    public void setGame(GameManager gameManager) {
        this.gameManager = gameManager;
    }
    
    public void setStage(Stage stage){
        this.stage = stage;
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
        showError(message, errorLabel);
    }
    
}