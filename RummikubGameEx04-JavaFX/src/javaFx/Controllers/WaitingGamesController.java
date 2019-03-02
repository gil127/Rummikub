package javaFx.Controllers;

import gameManager.GameManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class WaitingGamesController implements Initializable {
    
    @FXML 
    private TableView<String> waitingGameViewTable;
            
    @FXML
    private TableColumn<String, String> nameColumn;
    
    @FXML 
    private Button backButton;
    
    private GameManager gameManager;
    private Stage stage;
    private ArrayList<String> games;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        games = new ArrayList<String>();
    }
    
    @FXML
    protected void onBackButtonClicked(ActionEvent event) {
        waitingGameViewTable.getItems().clear();
        gameManager.setInitializeScene();
    }

    public void setGame(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setCurrentWaitingGames(List<String> waitingGames) {
        games.clear();
        games.addAll(waitingGames);
    }
    
    public void setTable(){
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        waitingGameViewTable.setItems(FXCollections.observableArrayList(games));
    }
}