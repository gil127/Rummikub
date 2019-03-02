package javaFx.Controllers;

import gameManager.GameManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class GameDetailsController implements Initializable {

    @FXML
    private Button backButton;
    
    @FXML
    private Label gameNameValueLabel;
    
    @FXML
    private Label numberOfPlayersValueLabel;
    
    @FXML
    private Label numberOfDigitalPlayersValueLabel;
    
    @FXML
    private Label numberOfHumanPlayersValueLabel;
    
    @FXML
    private Label numberOfHumanPlayersJoinedValueLabel;
    
    @FXML
    private Label gameStatusValueLabel;
    
    private GameManager gameManager;
    private Stage stage;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    
    @FXML
    protected void onBackButtonClicked(ActionEvent event) {
        gameManager.setInitializeScene();
    }

    public void setGame(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setGameNameLabel(String gameName) {
        gameNameValueLabel.setText(gameName);
    }

    public void setGameStatusLabel(String gameStatus) {
        gameStatusValueLabel.setText(gameStatus);
    }

    public void setNumberOfPlayersLabel(String numberOfPlayers) {
        numberOfPlayersValueLabel.setText(numberOfPlayers);
    }

    public void setNumberOfDigitalPlayersLabel(String numberOfDigitalPlayers) {
        numberOfDigitalPlayersValueLabel.setText(numberOfDigitalPlayers);
    }

    public void setNumberOfHumanPlayersLabel(String numberOfHumanPlayers) {
        numberOfHumanPlayersValueLabel.setText(numberOfHumanPlayers);
    }

    public void setNumberOfHumanPlayersJoinedLabel(String numberOfHumanPlayersJoined) {
        numberOfHumanPlayersJoinedValueLabel.setText(numberOfHumanPlayersJoined);
    }
}
