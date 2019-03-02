package javaFx;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneBuilder {
    public static final int CONNECT_TO_SERVER_SCENE_INDEX = 0;
    public static final int OPEN_SCENE_INDEX = 1;
    public static final int SETTING_SCENE_INDEX = 2;
    public static final int GAME_SESSION_INDEX = 3;
    public static final int GAME_OVER_INDEX = 4;
    public static final int WAITING_INDEX = 5;
    public static final int WAITING_GAME_INDEX = 6;
    public static final int GAME_DETAILS_INDEX = 7;
    public static final int PLAYERS_DETAILS_INDEX = 8;
    public static final int PLAYER_DETAILS_INDEX = 9;
    
    private static final String TITLE_CONNECT_TO_SERVER_SCENE = "Connect to server";
    private static final String TITLE_SETTING_SCENE = "Setting";
    private static final String TITLE_OPEN_SCENE = "Welcome to Rummikub!";
    private static final String TITLE_GAME_SCENE = "Rummikub Game";
    private static final String TITLE_GAME_OVER_SCENE = "Game Over";
    private static final String TITLE_WAITING_SCENE = "Waiting";
    private static final String TITLE_WAITING_GAME_SCENE = "Waiting Games";
    private static final String GAME_DETAILS_SCENE = "Game Details";
    private static final String PLAYERS_DETAILS_SCENE = "Players Details";
    private static final String PLAYER_DETAILS_SCENE = "Player Details";

    private static Stage primaryStage;
    private static final ArrayList<Scene> sceneOfTheGame = new ArrayList<Scene>();
    private static final ArrayList<Initializable> controllersOfTheGame = new ArrayList<Initializable>();
    private static ArrayList<String> titles = new ArrayList<String>();
    
    private SceneBuilder() {
    }
    
    public static void setTitles() {
        titles.add(TITLE_CONNECT_TO_SERVER_SCENE);
        titles.add(TITLE_OPEN_SCENE);        
        titles.add(TITLE_SETTING_SCENE);
        titles.add(TITLE_GAME_SCENE);
        titles.add(TITLE_GAME_OVER_SCENE);
        titles.add(TITLE_WAITING_SCENE);
        titles.add(TITLE_WAITING_GAME_SCENE);
        titles.add(GAME_DETAILS_SCENE);
        titles.add(PLAYERS_DETAILS_SCENE);
        titles.add(PLAYER_DETAILS_SCENE);
    }

    public static void setPrimaryStage(Stage primaryStage) {
        SceneBuilder.primaryStage = primaryStage;
    }

    public static <T> T buildNewScene(String pathOfScene) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();

        URL url = SceneBuilder.class.getResource(pathOfScene);
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(fxmlLoader.getLocation().openStream());
        T gameScene = (T) fxmlLoader.getController();
        Scene scene = new Scene(root);
        sceneOfTheGame.add(scene);
        controllersOfTheGame.add((Initializable)gameScene);
        return gameScene;
    }

    public static void loadSceneToStage(int indexOfScene) {
        primaryStage.setTitle(titles.get(indexOfScene));
        primaryStage.setScene(sceneOfTheGame.get(indexOfScene));
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
    }

    public static Object getSceneControler(int indexOfScene) {
        return controllersOfTheGame.get(indexOfScene);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void showPrimary() {
        primaryStage.show();
    }
}