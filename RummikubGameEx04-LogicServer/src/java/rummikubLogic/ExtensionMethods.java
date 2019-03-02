package rummikubLogic;

import generated.Board;
import generated.Tile;
import java.util.ArrayList;
import java.util.List;
import dataObject.Cube;

public abstract class ExtensionMethods {

    private static final int MIN_VALUE_FOR_CUBE = 1;
    private static final int MAX_VALUE_FOR_CUBE = 13;
    private static final int MIN_LEN_OF_SERIAL = 3;
     private static final int MAX_LEN_OF_SERIAL = 13;
    
    public static List<ArrayList<Cube>> replaceListOfTilesIntoListOfCubes(List<Board.Sequence> allSequence) {
        List<ArrayList<Cube>> allSerialsOfCubes = new ArrayList<ArrayList<Cube>> ();
        ArrayList<Cube> serial;
        for (Board.Sequence sequence : allSequence) {
            serial = replaceTilesIntoCubes(sequence.getTile(), true);
            ArrayList<Cube> sortedSerial = extensions.ExtensionMethods.sortSerial(serial);
            allSerialsOfCubes.add(sortedSerial);
        }
        
        return allSerialsOfCubes;
    }
    
    public static ArrayList<Cube> replaceTilesIntoCubes(List<Tile> tiles, boolean needToConvertJoker) {
        ArrayList<Cube> serial = new ArrayList<Cube>();
        boolean serialHasJoker = false;
        Cube cube;
        Cube joker = null;
        for (Tile tile : tiles) {
            if (tile.getValue() != 0) {
                Cube.Color color = Cube.Color.valueOf(tile.getColor().name());
                cube = new Cube(color, tile.getValue(), false);
            } else {
                Cube.Color color = Cube.Color.valueOf(tile.getColor().name());
                cube = new Cube(color, tile.getValue(), true);              
            }
            serial.add(cube);
            
            if (cube.isJoker() && needToConvertJoker) {
                serialHasJoker = true;
                joker = cube;
            }
        }
        
        if (serialHasJoker && joker != null) {
            extensions.ExtensionMethods.convertJoker(joker, serial);
        }
        
        return serial;
    }
    
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