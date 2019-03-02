package rummikubLogic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import dataObject.Cube;

public abstract class Player implements Score, Comparator<Score>
{   
    private static final int SUM_FOR_FIRST_SERIAL = 30;

    private final String name;
    protected List<Cube> backUpCubes;
    protected List<Cube> cubesStateWhilePlayerDoTheTurn;
    protected boolean canUseBoard;
    private boolean isComputer;
    protected int sumOfFirstDown;
    private boolean playerPlayCurrentTurn;
    private int score;
    private int scoreOfCurrentRound;
    private PlayerStatus status;
    private int ID;

    public Player(String nameOfPlayer, boolean isComputer) {
        this.name = nameOfPlayer;
        this.isComputer = isComputer;
        backUpCubes = new ArrayList<Cube>();
        cubesStateWhilePlayerDoTheTurn = new ArrayList<Cube>();
        score = 0;
        initNewRound();
        status = PlayerStatus.JOINED;
        ID = -1;
    }
    
    public final void initNewRound() {
        canUseBoard = false;
        sumOfFirstDown = 0;
        playerPlayCurrentTurn = false;
        clearAllCubes();
    }
    
    public boolean isComputer() {
        return isComputer;
    }
    
    public int getSumForFirstSerial() {
        return SUM_FOR_FIRST_SERIAL;
    }
    
    public String getName() {
        return name;
    }
    
    public void setDoneWithFirstSerial(boolean value) {
        canUseBoard = value;
    }
    
    public void addCubeToPlayerWhileInitialize(Cube cubeToAdd) {
        backUpCubes.add(cubeToAdd);
        cubesStateWhilePlayerDoTheTurn.add(cubeToAdd);
    }
    
    public void addCubeToPlayerWhileRunningGame(Cube cubeToAdd, boolean needToInsertToBackUp) {
        cubesStateWhilePlayerDoTheTurn.add(cubeToAdd);
        if (needToInsertToBackUp) {
            backUpCubes.add(cubeToAdd);
        }
    }
    
    public void removeCubesFromPlayer(Cube cubeToRemove) {
        cubesStateWhilePlayerDoTheTurn.remove(cubeToRemove);
        
        addValueToSumOfFirstSerial(cubeToRemove.getValue());
    }
            
    public List<Cube> getCubes() {
        return cubesStateWhilePlayerDoTheTurn;
    }
    
    public boolean isPlayerCanUseBoard() {
        return canUseBoard;
    }
    
    public int getNumerOfCubes() {
        return cubesStateWhilePlayerDoTheTurn.size();
    }
    
    public void updateListOfCubes(boolean valid) {
        if (valid) {
            backUpCubes = cubesStateWhilePlayerDoTheTurn;
            cubesStateWhilePlayerDoTheTurn = new ArrayList<Cube>();
            cubesStateWhilePlayerDoTheTurn.addAll(backUpCubes);
        }
        else {
            cubesStateWhilePlayerDoTheTurn = new ArrayList<Cube>();
            cubesStateWhilePlayerDoTheTurn.addAll(backUpCubes);
            
            if (canUseBoard && sumOfFirstDown > 0) {
                canUseBoard = false;
            }
        }
        
        sumOfFirstDown = 0;
    }
    
    public Cube getCubeWithSameData(Cube cubeToFind) {
        Cube res = null;
        
        for (Cube cube : cubesStateWhilePlayerDoTheTurn) {
            if (cubeToFind.isJoker()) {
                if (cube.isJoker()) {
                    res = cube;
                    cube.setTempCubeOfJokerCube(cubeToFind.getValue(), cubeToFind.getColor());                    
                    break;
                }
            }
            else if(cube.equals(cubeToFind)) {
                res = cube;
                break;
            }
        }
        return res;
    }
    
    public void setPlayerPlayCurrentTurn(boolean playerPlay) {
        playerPlayCurrentTurn = playerPlay;
    }

    public boolean isPlayerPlayCurrentTurn() {
        return playerPlayCurrentTurn && listNotEquals();
    }
    
    @Override
    public int getScore() {
        return score;
    }
    public void setScore(int value) {
        score = value;
    }
    
    public void addAndRemovePlayerToScore(int value) {
        scoreOfCurrentRound = value;
        this.score += value;
    }

    private void clearAllCubes() {
        cubesStateWhilePlayerDoTheTurn.clear();
        backUpCubes.clear();
    }

    @Override
    public int compare(Score o1, Score o2) {
        return o2.getScore() - o1.getScore();
    }
    
    public int getScoreOfCurrentRound() {
        return scoreOfCurrentRound;
    }

    public void setHandFromFile(ArrayList<Cube> playerHand) {
        backUpCubes = playerHand;
        cubesStateWhilePlayerDoTheTurn.addAll(playerHand);   
    }
    
    public boolean anyCubesDropedInTurn() {
        return backUpCubes.size() != cubesStateWhilePlayerDoTheTurn.size();
    }

    public boolean isCubeInBackUpAndNotInCurrList(Cube cube) {
        boolean res = false;
        if (backUpCubes.contains(cube) && !cubesStateWhilePlayerDoTheTurn.contains(cube)) {
            res = true;
        }
        
        return res;
    }

    void addValueToSumOfFirstSerial(int value) {
        if (!canUseBoard) {
            sumOfFirstDown += value;
            
            if (sumOfFirstDown >= SUM_FOR_FIRST_SERIAL) {
                canUseBoard = true;
            }
        }
    }

    public PlayerStatus getStatus() {
        return status;
    }
    
    public void setStatus(PlayerStatus value) {
        status = value;
    }

    public void setID(int playerID) {
        this.ID = playerID;
    }
    
    public int getID() {
        return ID;
    }

    private boolean listNotEquals() {
        return backUpCubes.size() != cubesStateWhilePlayerDoTheTurn.size();
    }

    public enum PlayerStatus {
        JOINED,
        ACTIVE,
        Wait,
        RETIRED;

        public String value() {
            return name();
        }

        public static PlayerStatus fromValue(String v) {
            return valueOf(v);
        }
    }
}