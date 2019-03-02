package javaFx.Controllers;

import fileManager.EnumsForFile;
import gameManager.GameManager;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import ws.rummikub.DuplicateGameName_Exception;
import ws.rummikub.InvalidParameters_Exception;


public class OpenSceneController implements Initializable{
	
    @FXML
    private Button createNewGameButton;
    
    @FXML
    private Button loadGameButton;
	
    @FXML
    private Button joinExistingGameButton;
    
    private Stage stage;

    @FXML
    private Text errorText;
	
    @FXML
    private TextField gameNameTextField;

    @FXML
    private ComboBox amountOfPlayersComboBox;
	
    @FXML
    private ComboBox amountOfDigitalPlayersComboBox;
	
    @FXML
    private Button setGameDetailButton;
    
    @FXML
    private Label gameNameLabel;
    
    @FXML
    private Label amountOfPlayersLabel;
    
    @FXML
    private Label amountOfDigitalPlayersLabel;
    
    @FXML
    private Button cancelButton;
    
    private GameManager gameManager;
    private String pathForXml;
    

    public void setGame(GameManager gameManager) {
        this.gameManager = gameManager;
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showDetailsForCreateGame(true);
        
        amountOfPlayersComboBox.setItems(FXCollections.observableArrayList(2, 3, 4));
        amountOfPlayersComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                onAmountOfPlayersSelected();
            }

            private void onAmountOfPlayersSelected() {
                amountOfDigitalPlayersComboBox.setDisable(false);
                int amountOfPlayerValue = 0;
                
                if (amountOfPlayersComboBox.getValue() instanceof Integer) {
                    amountOfPlayerValue = (int) amountOfPlayersComboBox.getValue();
                }
                
                ArrayList<Integer> listOfValues = new ArrayList<Integer>();
                for (int i = 0; i < amountOfPlayerValue; i++) {
                    listOfValues.add(i);
                }
                
                ObservableList<Integer> observableArrayList = FXCollections.observableArrayList(listOfValues);
                amountOfDigitalPlayersComboBox.setItems(observableArrayList);

            }
        });
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }
    
    @FXML
    private void onCreateNewGameClicked(ActionEvent event) {
        showErrorMessage("", Color.AQUA);
        showDetailsForCreateGame(false);
        disableBasicButtons(true);
    }

    @FXML
    private void onLoadGameClicked(ActionEvent event) {
        showErrorMessage("", Color.AQUA);
        pathForXml = getXmlPathFromUser();
        
        Thread thread = new Thread (this :: loadGameInDiffThread);
        thread.setDaemon(true);
        thread.start();
    }
    
    private void loadGameInDiffThread() {
        EnumsForFile.FileLoadResults validLoadedGame = gameManager.loadGameFromXml(pathForXml);
        
        switch(validLoadedGame) {
            case INVALID_XML: Platform.runLater(() -> showErrorMessage("The file is not found or the xml doesnt fit the schemma.", Color.RED));
                break;
            case INVALID_PARAMETERS: Platform.runLater(() -> showErrorMessage("The game in the file is not valid.", Color.RED));
                break;
            case DUPLICATE_GAME_NAME: Platform.runLater(() -> showErrorMessage("The game name already exist", Color.RED));
                break;
            default: Platform.runLater(() -> showErrorMessage("The game " + gameManager.getNameOfLastGameCreated() + " created", Color.BLUE));
                break;
        }
    }
    
    private void showErrorMessage(String errorMsg, Color color) {
        errorText.setText(errorMsg);
        errorText.setFill(color);
        FadeTransition animation = new FadeTransition();
        animation.setNode(errorText);
        animation.setDuration(Duration.seconds(0.3));
        animation.setFromValue(0.0);
        animation.setToValue(1.0);
        animation.play();
    }

    private String getXmlPathFromUser() {
        String xmlPath = null;
        File file = null;

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML Files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            xmlPath = file.getPath();
        }
        
        return xmlPath;
    }

    @FXML
    private void onJoinExistingGameClicked(ActionEvent event) {
        showErrorMessage("", Color.AQUA);
        gameManager.setInitializeScene();
    }
    
    private void showDetailsForCreateGame(boolean needToShow) {
        
        int opacity = needToShow? 0:1;
        
        gameNameLabel.setOpacity(opacity);
        gameNameTextField.setOpacity(opacity);
        amountOfPlayersLabel.setOpacity(opacity);
        amountOfDigitalPlayersLabel.setOpacity(opacity);
        amountOfDigitalPlayersComboBox.setOpacity(opacity);
        amountOfPlayersComboBox.setOpacity(opacity);
        setGameDetailButton.setOpacity(opacity);
        cancelButton.setOpacity(opacity);
        
        gameNameLabel.setDisable(needToShow);
        gameNameTextField.setDisable(needToShow);
        amountOfPlayersLabel.setDisable(needToShow);
        amountOfDigitalPlayersLabel.setDisable(needToShow);
        amountOfPlayersComboBox.setDisable(needToShow);
        setGameDetailButton.setDisable(needToShow);
        cancelButton.setDisable(needToShow);
    }
    
    private void disableBasicButtons(boolean needToDisable) {
        createNewGameButton.setDisable(needToDisable);
        loadGameButton.setDisable(needToDisable);
        joinExistingGameButton.setDisable(needToDisable);
    }
    
    @FXML
    private void onCancelButtonClicked(ActionEvent event) {
        showErrorMessage("", Color.AQUA);
        cleanGameDetails();
        showDetailsForCreateGame(true);
        disableBasicButtons(false);
    }

    private void cleanGameDetails() {
        gameNameTextField.setText("");
        amountOfPlayersComboBox.setValue(amountOfPlayersComboBox.getPromptText());
        amountOfDigitalPlayersComboBox.setItems(null);
    }

    @FXML
    private void onSetGameDetailButtonClicked (ActionEvent event) {
        String gameName = gameNameTextField.getText();
        if (gameName.equals("")) {
            showErrorMessage("The game name can't be empty", Color.RED);
        } 
        else {
            int numberOfPlayers = 0, numberOfDigitalPlayers = 0;
            try {
                numberOfPlayers = (int) amountOfPlayersComboBox.getValue();
                numberOfDigitalPlayers = (int) amountOfDigitalPlayersComboBox.getValue();

                new Thread(this::createGameInDiffThread).start();
            } catch (Exception ex) {
                showErrorMessage("Please insert number of players in the game", Color.RED);
            }
        }
    }
    
    
    private void createGameInDiffThread() {
        String gameName = gameNameTextField.getText();
        int numberOfPlayers = (int) amountOfPlayersComboBox.getValue();
        int numberOfDigitalPlayers = (int) amountOfDigitalPlayersComboBox.getValue();
        
        try {
            gameManager.createNewGame(gameName, numberOfPlayers, numberOfDigitalPlayers);
            
            Platform.runLater(() -> {
                disableBasicButtons(false);
                showDetailsForCreateGame(true);
                showErrorMessage("Game " + gameNameTextField.getText() + " Created!" , Color.BLUE);
                cleanGameDetails();
            });
        } catch (DuplicateGameName_Exception ex) {
            Platform.runLater(() -> showErrorMessage("The game name already exist", Color.RED));
        } catch (InvalidParameters_Exception ex) {
            Platform.runLater(() -> showErrorMessage("The amount of players you insert is not valid", Color.RED));
        }
    }
}
