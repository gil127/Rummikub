package javaFx.Controllers;

import gameManager.GameManager;
import java.net.URL;
import java.util.ResourceBundle;
import javaFx.SceneBuilder;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class GameOverController implements Initializable {
    @FXML
    private Button playNewGameButton;
	
    @FXML
    private Button exitButton;
	
    @FXML
    private Label winnerNameLabel;
    
    @FXML
    private Label winnerLabel;
	
    private Stage stage;
    private GameManager gameManager;
    
    public void setGame(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }
    
    
    @FXML
    private void playNewGameClicked(ActionEvent event) {
        gameManager.setNewGame();
        gameManager.setFirstScene();
    }
	
    @FXML
    private void exitClicked(ActionEvent event) {
        SceneBuilder.getPrimaryStage().close();
    }

    public void setWinnerNameToDisplay() {
        new Thread(()-> {
        String winnerName = gameManager.getWinnerName();
        Platform.runLater(()-> winnerNameLabel.setText(winnerName));
        }).start();
    }

    public void setPlayerRetired() {
        winnerNameLabel.setText("");
        winnerLabel.setText("You retired!\nBYE BYE!");
    }
}