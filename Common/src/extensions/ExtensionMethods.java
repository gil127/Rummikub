package extensions;

import dataObject.Cube;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.scene.image.Image;

public class ExtensionMethods {
    public static Image getImage (String filename, String imageExtension, String imageDir){
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        
        if (!filename.endsWith(imageExtension)){
            filename = filename + imageExtension;
        }
        
        return new Image(ExtensionMethods.class.getResourceAsStream(imageDir + filename));
    }
    
    public static ArrayList<Cube> sortSerial(ArrayList<Cube> serial) {
        ArrayList<Cube> res = serial;
        Collections.sort(serial, serial.get(0));
        boolean serialWithJump = checkForSerialWithJump(serial);
        
        if (serialWithJump) {
            ArrayList<Cube> newSerial = sortByJumpInSerial(serial);
            res = newSerial;
        }
        
        return res;
    }
    
    public static void sortAllSerials(List<ArrayList<Cube>> serials) {
        ArrayList<Cube> newSerial;
        for(int i = 0; i < serials.size(); i++) {
            newSerial = sortSerial(serials.get(i));
            
            if (!newSerial.equals(serials.get(i))) {
                serials.set(i, newSerial);
            }
        }
    }
    
    private static boolean checkForSerialWithJump(ArrayList<Cube> serial) {
        int lastIndexInSerial = serial.size() - 1;
        boolean res = false;
        
        if (serial.get(0).getValue() == Cube.MIN_VALUE_FOR_CUBE && serial.get(lastIndexInSerial).getValue() == Cube.MAX_VALUE_FOR_CUBE) {
            res = true;
        }
        
        return res;
    }
    
    private static ArrayList<Cube> sortByJumpInSerial(ArrayList<Cube> serial) {
        ArrayList<Cube> res = new ArrayList<Cube>();
        int k = 1;
        boolean flag = false;
        
        // find the elements with jumping between them
        for (int i = 1; i < serial.size() && !flag; i++) {
            if (serial.get(i).getValue() -  serial.get(i-1).getValue() > 1) {
                res.add(serial.get(i));
                flag = true;
            }
            k++;
        }
        
        // copy from jumping elements until the end
        for (int i = k; i < serial.size(); i++) {
            res.add(serial.get(i));
        }
        
        // copy from biggest to element begfore jumping
        for(int i = 0; i < k - 1; i++) {
            res.add(serial.get(i));
        }
        
        return res;
    }
    
    public static void convertJoker(Cube joker, ArrayList<Cube> serial) {
        if (checkIfSequenceHasSameValue(serial)) {
            setJokerInValidPositionAtSameValueSerial(joker, serial);
        } else {
            setJokerInValidPositionAtSameColorSerial(joker, serial);
        }
    }

    private static boolean checkIfSequenceHasSameValue(List<Cube> cubes) {
        boolean res = false;
        int value = 0;
        
        for (Cube cube : cubes) {
            if (!cube.isJoker()) {
                if (value != 0) {
                    if (value != cube.getValue()) {
                        res = false;
                        break;
                    }
                    else {
                        res = true;
                    }
                }
                else {
                    value = cube.getValue();
                }
            }
        }
        
        return res;
    }
    
    private static Cube setJokerInValidPositionAtSameValueSerial(Cube theJoker, List<Cube> sameValueSerial) {
        boolean blackAppeared = false;
        boolean blueAppeared = false;
        boolean redAppeared = false;
        boolean yellowAppeared = false;
        int value = 0;

        for (Cube cube : sameValueSerial) {
            if (!cube.isJoker()) {
                value = cube.getValue();
                Cube.Color colorOfCube = cube.getColor();
                switch (colorOfCube) {
                    case BLACK:
                        blackAppeared = true;
                        break;
                    case BLUE:
                        blueAppeared = true;
                        break;
                    case YELLOW:
                        yellowAppeared = true;
                        break;
                    case RED:
                        redAppeared = true;
                        break;
                }
            }
        }
   
        Cube.Color cubeColor;

        if (!blackAppeared) {
            cubeColor = Cube.Color.BLACK;
        } else if (!yellowAppeared) {
            cubeColor = Cube.Color.YELLOW;
        } else if (!redAppeared) {
            cubeColor = Cube.Color.RED;
        } else {
            cubeColor = Cube.Color.BLUE;
        }

        theJoker.setTempCubeOfJokerCube(value, cubeColor);

        return theJoker;
    }
    
    public static Cube setJokerInValidPositionAtSameColorSerial(Cube joker, List<Cube> serial) {
        Collections.sort(serial, serial.get(0));
        boolean jokerChangeValue = false;
        Cube.Color colorOfCubeInSerial;
        
        if (serial.size() >= 2) {
            int previousValue = serial.get(serial.size() - 1).getValue();
            int currentValue;
            
            for (int i = serial.size() - 2; i >= 0; i--) {
                if (!serial.get(i).isJoker()) {
                    colorOfCubeInSerial = serial.get(i).getColor();
                    currentValue = serial.get(i).getValue();

                    if (currentValue + 2 == previousValue) {
                        joker.setTempCubeOfJokerCube(currentValue + 1, colorOfCubeInSerial);
                        jokerChangeValue = true;
                        break;
                    }
                    previousValue = currentValue;
                }
            }
            
            if (jokerChangeValue) {
                Collections.sort(serial, serial.get(0));
            }
            else { 
                int lastCubeValue = serial.get(serial.size() - 1).getValue();
                colorOfCubeInSerial = serial.get(serial.size() - 1).getColor();
                if (lastCubeValue != 13) {
                    joker.setTempCubeOfJokerCube(lastCubeValue + 1, colorOfCubeInSerial);
                }
                else {
                    joker.setTempCubeOfJokerCube(getValueOfCubeBeforeJump(serial) + 1, colorOfCubeInSerial);
                }
            }
        }
        
        return joker;
    }
    
    private static int getValueOfCubeBeforeJump(List<Cube> serial) {
        int res = 0, lastValueOfCube = 0, currValueOfCube;
        int index = 0;
        
        for (Cube cube : serial) {
            if (!cube.isJoker()) {
                lastValueOfCube = cube.getValue();
                break;
            }
            index ++ ;
        }
        
        for (int i = index + 1; i < serial.size(); i++) {
            currValueOfCube = serial.get(i).getValue();
            
            if (currValueOfCube > lastValueOfCube + 1) {
                res = lastValueOfCube;
                break;
            }
            else {
                lastValueOfCube = currValueOfCube;
            }
        }
        
        return res;
    }
}
