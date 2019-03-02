/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaFx.Controllers;

import gameManager.GameManager;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import ws.rummikub.PlayerDetails;
import ws.rummikub.Tile;

public class PlayerDetailsController implements Initializable{

    @FXML
    private Button backButton;
    
    @FXML
    private Label playerNameValueLabel;
    
    @FXML
    private Label playerIdValueLabel;
    
    @FXML
    private FlowPane playerTilesFlowPane;
    
    private GameManager gameManager;
    private Stage stage;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    
    public void setGame(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    @FXML
    private void onBackButtonClicked(ActionEvent event) {
        gameManager.setGameScene();
    }
    
    private void setPlayerNameLabel(String playerName) {
        playerNameValueLabel.setText(playerName);
    }
    
    private void setPlayerTiles(List<Tile> playerTiles) {
        //FlowPane.
    }
    
    private void setPlayerIdLabel(String playerId) {
        playerIdValueLabel.setText(playerId);
    }
    
    public void setSceneDetails(PlayerDetails playerDetails) {
        setPlayerNameLabel(playerDetails.getName());
        setPlayerIdLabel(gameManager.getPlayerId());
        setPlayerTiles(playerDetails.getTiles());
    }
    
}
