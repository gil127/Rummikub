package dataObject;

public class CubeBoard {
    private final Cube cube;
    private int numOfSerial;
    
    public CubeBoard(Cube cube, int numOfSerial) {
        this.cube = cube;
        this.numOfSerial = numOfSerial;
    }
    
    public Cube getCube() {
        return cube;
    }
    
    public int getNumOfSerial() {
        return numOfSerial;
    }

    public void setNumOfSerial(int newNumOfSerial) {
        this.numOfSerial = newNumOfSerial;
    }
}
