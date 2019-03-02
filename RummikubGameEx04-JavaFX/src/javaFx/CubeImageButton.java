package javaFx;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import dataObject.Cube;

public class CubeImageButton extends Button{
    Cube cube;
    
    public CubeImageButton(Cube cube, Image image) {
        super("", new ImageView(image));
        this.cube = cube;
    }
    
    public Cube getCube() {
        return cube;
    }
}
