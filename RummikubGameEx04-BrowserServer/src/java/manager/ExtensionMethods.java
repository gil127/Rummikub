package manager;

import java.util.ArrayList;
import java.util.List;
import dataObject.Cube;

public abstract class ExtensionMethods {

    private static final int MIN_VALUE_FOR_CUBE = 1;
    private static final int MAX_VALUE_FOR_CUBE = 13;
    private static final int MIN_LEN_OF_SERIAL = 3;
     private static final int MAX_LEN_OF_SERIAL = 13;
    
    public static boolean isSerialsValid(List<ArrayList<Cube>> serials) {
        boolean res = true;

        for (ArrayList<Cube> serial : serials) {
            if (serial.size() >= MIN_LEN_OF_SERIAL && serial.size() <= MAX_LEN_OF_SERIAL) {
                res = checkCurrentSerial(serial);
            }
            else { 
                res = false;
            }
            
            if (!res) {
                break;
            }
        }
        
        return res;
    }

    public static boolean checkCurrentSerial(ArrayList<Cube> serial) {
        int indexOfFirstCube = 0;
        boolean res = false;
        
        Cube firstCube = serial.get(indexOfFirstCube);
        Cube secondCube = serial.get(indexOfFirstCube + 1);
        
        if (!secondCube.equals(firstCube)) {
            if (secondCube.getColor() != firstCube.getColor()) {
                res = checkSameValueSerial(serial, firstCube.getValue());
            }
            else {
                res = checkSameColorSerial(serial, firstCube.getColor(), firstCube.getValue());
            }
        }
        else {
            res = false;
        }
        
        return res;
    }
    
    private static boolean checkSameColorSerial(ArrayList<Cube> serial, Cube.Color serialColor, int firstCubeValue) {
        boolean isValid = true;
        int lastCubeCheckedValue = firstCubeValue;
        Cube firstCube = serial.get(0);
        
        for (Cube cube : serial) {
            if (cube != firstCube) {
                if (cube.getColor() != serialColor) {
                    isValid = false;
                    break;
                }
                else {
                    if (cube.getValue() == lastCubeCheckedValue + 1) {
                        lastCubeCheckedValue = cube.getValue();
                    }
                    else {
                        if (cube.getValue() == MIN_VALUE_FOR_CUBE && lastCubeCheckedValue == MAX_VALUE_FOR_CUBE) {
                            lastCubeCheckedValue = cube.getValue();
                        }
                        else {
                            isValid = false;
                            break;
                        }
                    }
                }
            }
        }
        
        return isValid;
    }

    private static boolean checkSameValueSerial(ArrayList<Cube> serial, int serialValue) {
        boolean isValid = true;
        boolean blackAppeared = false;
        boolean yellowAppeared = false;
        boolean blueAppeared = false;
        boolean redAppeared = false;
        
        for (Cube cube : serial) {
            if (cube.getValue() != serialValue) {
                isValid = false;
                break;
            }
            else {
                Cube.Color colorOfCube = cube.getColor();
                switch (colorOfCube) {
                    case BLACK : 
                        if (blackAppeared) {
                            isValid = false;
                            break;
                        }
                        else {
                            blackAppeared = true;
                        }
                    break;
                    case BLUE: 
                        if (blueAppeared) {
                            isValid = false;
                            break;
                        }
                        else {
                            blueAppeared = true;
                        }
                    break;
                    case YELLOW:
                        if (yellowAppeared) {
                            isValid = false;
                            break;
                        }
                        else {
                            yellowAppeared = true;
                        }
                    break;
                    case RED:
                        if (redAppeared) {
                            isValid = false;
                            break;
                        }
                        else {
                            redAppeared = true;
                        }
                    break;
                }
            }
        }
        
        return isValid;
    }
}