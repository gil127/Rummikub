package javaFx.Controllers;

import gameManager.GameManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ConnectToServerController implements Initializable {

    @FXML
    private Button continueButton;
	
    @FXML
    private TextField ipTextField;
	
    @FXML
    private TextField portTextField;
	
    @FXML
    private Label errorMessage;
    
    @FXML
    private Label ipLabel;
    
    @FXML
    private Label portLabel;
    
    private GameManager gameManager;
    private Stage stage;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        portTextField.setDisable(true);
        
            ipTextField.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                onIpAdderssChange();
            }

            private void onIpAdderssChange() {
                portTextField.setDisable(false);
            }
        });
    }

    @FXML
    private void continueClicked(ActionEvent event) {
        boolean ipAndPortNotValid = checkIfTextFieldValid();
        if (ipAndPortNotValid) {
            reportToUserToFillTextField();
        }
        else {
            errorMessage.setText("");
            gameManager.configServer(ipTextField.getText(), portTextField.getText());
        }
    }

    private boolean checkIfTextFieldValid() {
        return portTextField.getText().isEmpty() || ipTextField.getText().isEmpty();
    }

    private void reportToUserToFillTextField() {
        if (portTextField.getText().isEmpty() && ipTextField.getText().isEmpty()) {
            errorMessage.setText("Type the server ip address and the port address");
        }
        else if (ipTextField.getText().isEmpty()) {
            errorMessage.setText("Type the server ip address");
        }
        else if (portTextField.getText().isEmpty()) {
            errorMessage.setText("Type the port address");
        }
    }

    public void setGame(GameManager game) {
        gameManager = game;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void showError(String error) {
        errorMessage.setText(error);
        errorMessage.setTextFill(Color.RED);
    }
}