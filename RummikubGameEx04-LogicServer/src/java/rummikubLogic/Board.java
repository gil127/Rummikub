package rummikubLogic;

import java.util.ArrayList;
import java.util.List;
import dataObject.Cube;
import dataObject.CubeBoard;

public class Board 
{
     private final int MIN_LEN_OF_SERIAL = 3;
     private final int MAX_LEN_OF_SERIAL = 13;
     private final int MIN_VALUE_FOR_CUBE = 1;
     private final int MAX_VALUE_FOR_CUBE = 13;
     
     private List<ArrayList<Cube>> backUpCubes;
     private List<ArrayList<Cube>> cubesStateWhilePlayerDoTheTurn;
    
    public Board() {
        backUpCubes = new ArrayList<>();
        cubesStateWhilePlayerDoTheTurn = new ArrayList<ArrayList<Cube>>();
    }
    
    public int getBoardSize() {
        int size = 0;
        
        for (int i = 0; i < cubesStateWhilePlayerDoTheTurn.size(); i++) {
            for (ArrayList<Cube> serial : cubesStateWhilePlayerDoTheTurn) {
                size += serial.size();
            }
        }
        
        return size;
    }
    
    public List<ArrayList<Cube>> getCubes() {
        return cubesStateWhilePlayerDoTheTurn;
    }
    
    public void addSerialToBoard(ArrayList<Cube> serial) {
        cubesStateWhilePlayerDoTheTurn.add(serial);
    }
    
    public void updateListOfCubes(boolean valid) {
        if (valid) {
            backUpCubes = cubesStateWhilePlayerDoTheTurn;
            copyTempListIntoNewOne();
        }
        else {
            copyTempListIntoNewOne();
        }
    }

    public int getNumOfSerials() {
        return cubesStateWhilePlayerDoTheTurn.size();
    }

    public Cube getCubeWithSameData(CubeBoard cubeBoard, boolean actionMayNeedSplit) {
        List<Cube> serial = cubesStateWhilePlayerDoTheTurn.get(cubeBoard.getNumOfSerial() - 1);
        Cube res = null;
        boolean needToSplit = false;
        int index = 0;
        ArrayList<Cube> newSerial = new ArrayList<>();
        Cube cubeToFind = cubeBoard.getCube();
        boolean serialWithSameNumber = false;
        
        if (serial.size() > 0) {
            if (serial.get(0).getColor() != cubeToFind.getColor() && serial.get(0).getValue() == cubeToFind.getValue()) {
                serialWithSameNumber = true;
            }
        }
        
        for (Cube cube : serial) {
            if (cube.equals(cubeToFind)) {
                res = cube;
                
                if (cube.isJoker()) {
                    cube.setTempCubeOfJokerCube(cubeToFind.getValue(), cubeToFind.getColor());
                }
                
                if (index != 0 && index != (serial.size() - 1) && !serialWithSameNumber && actionMayNeedSplit) {
                    needToSplit = true;
                }
                else {
                    break;
                }
            }
            else if (needToSplit) {
                newSerial.add(cube);
            }
            index++;
        }
        
        if (needToSplit) {
            serial.removeAll(newSerial);
            
            if (newSerial.size() > 0) {
                cubesStateWhilePlayerDoTheTurn.add(newSerial);
            }
            if (serial.size() == 0) {
                cubesStateWhilePlayerDoTheTurn.remove(serial);
            }
        }
        
        return res;
    }

    public boolean removeCubeFromSerial(Cube cubeToRemove, int serialNumber) {
        ArrayList<Cube> serial = cubesStateWhilePlayerDoTheTurn.get(serialNumber - 1);
        serial.remove(cubeToRemove);
        boolean removeSerial = false;
        
        if (serial.size() == 0) {
            cubesStateWhilePlayerDoTheTurn.remove(serial);
            removeSerial = true;
        }
       
        return removeSerial;
    }

    public void insertCubeToEdgeOfSerial(Cube cubeToInsert, int serialNumber, boolean insertToEnd) {
        if (cubeToInsert.isJoker()) {
            extensions.ExtensionMethods.convertJoker(cubeToInsert, cubesStateWhilePlayerDoTheTurn.get(serialNumber - 1));
        }
        
        if (insertToEnd) {
            cubesStateWhilePlayerDoTheTurn.get(serialNumber - 1).add(cubeToInsert);
        }
        else {
            cubesStateWhilePlayerDoTheTurn.get(serialNumber - 1).add(0, cubeToInsert);
        }
    }

    private void copyTempListIntoNewOne() {
        cubesStateWhilePlayerDoTheTurn = new ArrayList<ArrayList<Cube>>();
        for(ArrayList<Cube> serial : backUpCubes) {
            ArrayList<Cube> newSerial = new ArrayList<Cube> (serial);
            cubesStateWhilePlayerDoTheTurn.add(newSerial);
        }    
    }

    public Cube replaceJokerInCubeByLineSerialNumber(Cube alternativeCube, int serialNumber) {
        Cube res = null;
        int indexForAlternativeCube = 0;
        
        ArrayList<Cube> currentSerial = cubesStateWhilePlayerDoTheTurn.get(serialNumber - 1);
        
        for (Cube cube : currentSerial) {
            if (cube.isJoker()) {
                res = cubesStateWhilePlayerDoTheTurn.get(serialNumber - 1).set(indexForAlternativeCube, alternativeCube);
                break;
            }
            indexForAlternativeCube++;
        }
        
        return res;
    }

    public void clearBoard() {
        backUpCubes.clear();
        cubesStateWhilePlayerDoTheTurn.clear();
    }

    public void setBoardFromFile(List<ArrayList<Cube>> boardSerialsFromFile) {
        backUpCubes = boardSerialsFromFile;
        copyTempListIntoNewOne();
    }

    public ArrayList<Cube> getListOfAllCubes() {
        ArrayList<Cube> allCubes = new ArrayList<Cube>();
        
        for(ArrayList<Cube> serial : cubesStateWhilePlayerDoTheTurn) {
            allCubes.addAll(serial);
        }
        
        return allCubes;
    }

    public void merge2Serials(int destSerialNum, int sourceSerialNum) {
        List<Cube> sourceSerial = cubesStateWhilePlayerDoTheTurn.get(sourceSerialNum - 1);
        cubesStateWhilePlayerDoTheTurn.get(destSerialNum - 1).addAll(sourceSerial);
        cubesStateWhilePlayerDoTheTurn.remove(sourceSerial);
    }

    public int getIndexOfCubeInSerial(Cube cubeToInsert, int serialNumber) {
        int position = 0;
        List<Cube> serial = cubesStateWhilePlayerDoTheTurn.get(serialNumber - 1);
        
        for (Cube cube : serial) {
            if (cubeToInsert.equals(cube)) {
                break;
            }
            
            position ++;
        }
        
        return position;
    }

    public Cube getCubeFromSpecificLocation(int sequenceIndex, int sequencePosition) {
        if (sequenceIndex <= 0 || sequenceIndex > cubesStateWhilePlayerDoTheTurn.size()) {
            return null;
        }
        
        List<Cube> serial = cubesStateWhilePlayerDoTheTurn.get(sequenceIndex - 1);
        
        if (sequencePosition < 0 || sequencePosition >= serial.size()) {
            return null;
        }
        
        return serial.get(sequencePosition);
    }

    List<Cube> getSerial(int serialNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}