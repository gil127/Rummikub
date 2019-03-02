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
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import ws.rummikub.PlayerDetails;
import ws.rummikub.PlayerType;


public class PlayersDetailsController implements Initializable{
    
    @FXML
    private TableView playersDetailsTableView;
    
    @FXML
    private TableColumn<PlayerDetails, String> playerNameColumn;
    
    @FXML
    private TableColumn<PlayerDetails, String> playerTypeColumn;
    
    @FXML
    private TableColumn<PlayerDetails, Integer> numberOfTilesColumn;
    
    @FXML
    private TableColumn<PlayerDetails, String> placeSequenceColumn;
    
    @FXML
    private Button backButton;
    
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
        gameManager.startGame();
    }

    public void setPlayersDetails(List<PlayerDetails> playersDetails) {
        placeSequenceColumn.setCellValueFactory((data) -> {
            PlayerDetails value = data.getValue();
            String cellValue = value.isPlayedFirstSequence()? "True" : "False";
                return new ReadOnlyStringWrapper(cellValue);
        });
        
        numberOfTilesColumn.setCellValueFactory((data) -> {
            PlayerDetails value = data.getValue();
            Integer cellValue = value.getNumberOfTiles();
            return new ReadOnlyObjectWrapper<Integer>(cellValue);
        });
        
        playerNameColumn.setCellValueFactory((data) -> {
            PlayerDetails value = data.getValue();
            String cellValue = value.getName();
            return new ReadOnlyStringWrapper(cellValue);
        });
        
        playerTypeColumn.setCellValueFactory((data) -> {
            PlayerDetails value = data.getValue();
            String cellValue = value.getType() == PlayerType.HUMAN? "Human" : "Computer";
            return new ReadOnlyStringWrapper(cellValue);
        });
        
        playersDetailsTableView.setItems(FXCollections.observableArrayList(playersDetails));
    }
}
