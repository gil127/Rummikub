package dataObject;

import java.util.ArrayList;

public class Action {
    private ArrayList<Cube> playerCubes;
    private ArrayList<CubeBoard> boardCubes;
    private ActionType action;
    private int serialNumber;
    
    public Action() {
        playerCubes = new ArrayList<Cube>();
        boardCubes = new ArrayList<CubeBoard>();
    }
    
    public void setActionType(ActionType action) {
        this.action = action;
    }
    
    public ActionType getActionType() {
        return action;
    }
    
    public void addPlayerCubes(Cube cube) {
        playerCubes.add(cube);
    }
    
    public ArrayList<Cube> getPlayerCubes() {
        return playerCubes;
    }
    
    public void addBoardCube(CubeBoard cube) {
        boardCubes.add(cube);
    }
    
    public ArrayList<CubeBoard> getBoardCube() {
        return boardCubes;
    }
    
    public int getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }
}
