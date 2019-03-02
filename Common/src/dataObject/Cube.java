package dataObject;

import java.util.Comparator;

public class Cube implements Comparator<Cube> {

    public enum Color {

        BLUE(1), RED(2), YELLOW(3), BLACK(4);

        private int value;

        private Color(int value) {
            this.value = value;
        }
    }
    
    public static final int MIN_VALUE_FOR_CUBE = 1;
    public static final int MAX_VALUE_FOR_CUBE = 13;
    

    private final Color color;
    private final int value;
    private final boolean isJoker;
    private Color colorOfJokerCube;
    private int valueOfJokerCube;
    
    public Cube(Color color, int value, boolean isJoker) {
        this.isJoker = isJoker;
        this.color = color;
        colorOfJokerCube = color;
        this.value = value;
        valueOfJokerCube = value;
    }
    public Color getColor() {
        if (isJoker) {
            return colorOfJokerCube;
        }
        else {
            return color;
        }
    }
    
    public int getValue() {
        if (isJoker) {
            return valueOfJokerCube;
        }
        else {
            return value;
        }
    }
            
    public boolean isJoker () {
        return isJoker;
    }
    
    public void setTempCubeOfJokerCube(int value, Color color) {
        if (isJoker) {
            colorOfJokerCube = color;
            valueOfJokerCube = value;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Cube other = (Cube) obj;
        
        if (this.isJoker && other.isJoker()) {
            return true;
        }
        
        if (this.getColor() != other.getColor() || this.getValue() != other.getValue()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + this.value;
        return hash;
    }
    
    @Override
    public int compare(Cube o1, Cube o2) {
        if (o1.getColor() == o2.getColor()) {
            return o1.getValue() - o2.getValue();
        }
        else {
            return 0;
        }
    }
    
}