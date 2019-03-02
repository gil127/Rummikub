package rummikubgameex04;

import gameManager.GameManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author or
 */
public class RummikubGameEx04JavaFX extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        GameManager game = new GameManager();
        
        try {
            game.run(primaryStage);
        } catch (IOException ex) {
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
