package javaFx.Controllers;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class PlayerView extends VBox{
    private static final String RESOURCES_DIR = "/resources/";
    private static final String IMAGES_DIR = RESOURCES_DIR + "Player's Images/";
    private static final String IMAGE_EXTENSION = ".png";
    
    public PlayerView(String title, boolean isHuman) {
        setSpacing(10);
        setAlignment(Pos.CENTER);
        getChildren().addAll(createImage(isHuman), createLabel(title));
    }
    
    private Label createLabel(String title){
        return new Label(title);
    }
    
    private ImageView createImage(boolean isHuman){
        return new ImageView(getImage(isHuman));
    }

    private Image getImage(boolean isHuman) {
        String filename = isHuman ? "human" : "computer";
        return getImageFromPath(filename);
    }
    
    private Image getImageFromPath(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        
        if (!fileName.endsWith(IMAGE_EXTENSION)){
            fileName = fileName + IMAGE_EXTENSION;
        }
        
        return new Image(PlayerView.class.getResourceAsStream(IMAGES_DIR + fileName));
    }
}