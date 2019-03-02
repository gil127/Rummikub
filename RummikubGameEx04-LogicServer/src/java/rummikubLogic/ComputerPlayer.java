package rummikubLogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import dataObject.Cube;
import dataObject.Cube.Color;
import dataObject.CubeBoard;
import dataObject.Action;
import dataObject.ActionType;

public class ComputerPlayer extends Player{

    public ComputerPlayer(String nameOfPlayer, boolean isComputer) {
        super(nameOfPlayer, isComputer);
    }

    private ArrayList<Cube> createValidSerialFromPlayerCube(ArrayList<Cube> currentPlayerCubes) {
        ArrayList<Cube> sameColorSerial = new ArrayList<>();
        ArrayList<Cube> sameValueSerial = new ArrayList<>();
        ArrayList<Cube> validSerial = null;
        boolean jokerAlreadyBeenAdded = false;

        for (Cube currentCube : currentPlayerCubes) {
            sameColorSerial.add(currentCube);
            sameValueSerial.add(currentCube);
            for (Cube cube : currentPlayerCubes) {
                if (!currentCube.equals(cube)) {
                    if (cube.isJoker() && !jokerAlreadyBeenAdded) {
                       sameColorSerial.add(cube); 
                       sameValueSerial.add(cube);
                       jokerAlreadyBeenAdded = true;
                    }
                    else  if (currentCube.getColor() != cube.getColor() && currentCube.getValue() == cube.getValue()) {
                        sameValueSerial.add(cube);
                    } else if (currentCube.getColor() == cube.getColor()) {
                        sameColorSerial.add(cube);
                    }
                }
            }
            
            validSerial = checkIfHasValidSerial(sameColorSerial, sameValueSerial);
            if (validSerial != null && validSerial.size() >= 3) {
                break;
            } else {
                sameColorSerial.clear();
                sameValueSerial.clear();
            }
        }

        return validSerial;
    }

    private ArrayList<Cube> checkIfHasValidSerial(ArrayList<Cube> sameColorSerial, ArrayList<Cube> sameValueSerial) {
        ArrayList<Cube> res = null;

        if (sameValueSerial.size() >= 3 && validSameValueSerial(sameValueSerial)) {
            res = sameValueSerial;
        } else if (sameColorSerial.size() >= 3) {
            res = validSameColorSerial(sameColorSerial);
        }

        return res;
    }

    private boolean validSameValueSerial(List<Cube> sameValueSerial) {
        boolean isValid = true;
        boolean blackAppeared = false;
        boolean blueAppeared = false;
        boolean redAppeared = false;
        boolean yellowAppeared = false;
        Cube theJoker = null;
        int valueOfCubeInSerial = 0;

        for (Cube cube : sameValueSerial) {
            if (!cube.isJoker()) {
                valueOfCubeInSerial = cube.getValue();
                Cube.Color colorOfCube = cube.getColor();
                switch (colorOfCube) {
                    case BLACK:
                        if (blackAppeared) {
                            isValid = false;
                            break;
                        } else {
                            blackAppeared = true;
                        }
                        break;
                    case BLUE:
                        if (blueAppeared) {
                            isValid = false;
                            break;
                        } else {
                            blueAppeared = true;
                        }
                        break;
                    case YELLOW:
                        if (yellowAppeared) {
                            isValid = false;
                            break;
                        } else {
                            yellowAppeared = true;
                        }
                        break;
                    case RED:
                        if (redAppeared) {
                            isValid = false;
                            break;
                        } else {
                            redAppeared = true;
                        }
                        break;
                }
            }
            else {
                theJoker = cube;
            }
        }
        
        Color cubeColor;
        
        if (isValid && theJoker != null) {
            if (sameValueSerial.size() < 4) {
                if (!blackAppeared) {
                    cubeColor = Color.BLACK;
                }
                else if (!yellowAppeared) {
                    cubeColor = Color.YELLOW;
                }
                else if (!redAppeared) {
                    cubeColor = Color.RED;
                }
                else {
                    cubeColor = Color.BLUE;
                }
                
                theJoker.setTempCubeOfJokerCube(valueOfCubeInSerial, cubeColor);
            }
            else {
                sameValueSerial.remove(theJoker);
            }
        }

        return isValid;
    }

    private ArrayList<Cube> validSameColorSerial(List<Cube> sameColorSerial) {
        boolean isValid = false;
        Collections.sort(sameColorSerial, sameColorSerial.get(0));
        int serialSize = sameColorSerial.size();
        int indexOfCubeStartSerial = serialSize - 1;
        int indexOfCubeFinishSerial = indexOfCubeStartSerial, previousCubeValue, currentCubeValue;
        int numberOfCubesInValidSerial = 1;
        ArrayList<Cube> res = null;
        
        if (sameColorSerial.get(0).isJoker()) { // Joker has value of 0, that why after the sort, the joker will be in index 0.
            extensions.ExtensionMethods.setJokerInValidPositionAtSameColorSerial(sameColorSerial.get(0), sameColorSerial);
        } 

        previousCubeValue = sameColorSerial.get(indexOfCubeStartSerial).getValue();

        for (int i = serialSize - 2; i >= 0; i--) {
            currentCubeValue = sameColorSerial.get(i).getValue();
            
            if (currentCubeValue != 0) {
                if (previousCubeValue - 1 == currentCubeValue) {
                    indexOfCubeStartSerial = i;
                    numberOfCubesInValidSerial++;
                    previousCubeValue = currentCubeValue;
                } else if (numberOfCubesInValidSerial < 3) {
                    previousCubeValue = currentCubeValue;
                    indexOfCubeStartSerial = i;
                    indexOfCubeFinishSerial = i;
                    numberOfCubesInValidSerial = 1;
                } else {
                    isValid = true;
                }
            }
            else {
                sameColorSerial.get(i).setTempCubeOfJokerCube(13, sameColorSerial.get(serialSize - 1).getColor());
            }
        }

        if (numberOfCubesInValidSerial >= 3) {
            isValid = true;
        }

        if (!isValid) {
            if (sameColorSerial.get(serialSize - 1).getValue() == 13 && sameColorSerial.get(0).getValue() == 1) {
                res = new ArrayList<Cube>();
                res.add(sameColorSerial.get(0));
                res.add(sameColorSerial.get(serialSize - 1));
                previousCubeValue = 1;

                for (int i = 1; i < serialSize; i++) {
                    currentCubeValue = sameColorSerial.get(i).getValue();
                    if (previousCubeValue + 1 == currentCubeValue) {
                        previousCubeValue = currentCubeValue;
                        res.add(sameColorSerial.get(i));
                    } else {
                        break;
                    }
                }

                if (previousCubeValue != 12) {
                    previousCubeValue = 13;
                    for (int i = serialSize - 2; i >= 0; i--) {
                        currentCubeValue = sameColorSerial.get(i).getValue();
                        if (previousCubeValue - 1 == currentCubeValue) {
                            previousCubeValue = currentCubeValue;
                            res.add(sameColorSerial.get(i));
                        } else {
                            break;
                        }
                    }
                }

                if (res.size() < 3) {
                    res.clear();
                    res = null;
                }
            }
        } 
        else {
            res = new ArrayList<Cube>();
            for (int i = 0; i < serialSize; i++) {
                if (i >= indexOfCubeStartSerial && i <= indexOfCubeFinishSerial) {
                    res.add(sameColorSerial.get(i));
                }
            }
            
            previousCubeValue = res.get(0).getValue();
            
            for (int i = 1; i < res.size(); i++) {
                currentCubeValue = res.get(i).getValue();
                
                if (currentCubeValue == previousCubeValue) {
                    res.remove(i);
                }
                
                previousCubeValue = currentCubeValue;
            }
        }

        return res;
    }

    private Action createAction(ActionType actionType, ArrayList<Cube> playerCube, ArrayList<CubeBoard> boardCube, int serialNumber) {
        Action action = new Action();
        action.setActionType(actionType);

        if (playerCube != null) {
            for (Cube cube : playerCube) {
                action.addPlayerCubes(cube);
            }
        }

        if (boardCube != null) {
            for (CubeBoard cube : boardCube) {
                action.addBoardCube(cube);
            }
        }

        action.setSerialNumber(serialNumber);

        boolean playerPlayHisTurn = actionType != ActionType.TAKE_FROM_BANK;

        setPlayerPlayCurrentTurn(playerPlayHisTurn);

        return action;
    }

    public ArrayList<Action> createValidActions(List<ArrayList<Cube>> boardCubes) {
        ArrayList<Action> actions = new ArrayList<Action>();
        int serialNumber = 0;
        boolean hasAnotherAction = true;
        boolean firstAction = true;
        ArrayList<Cube> currentPlayerCubes = new ArrayList<>();
        currentPlayerCubes.addAll(cubesStateWhilePlayerDoTheTurn);
        
        while (hasAnotherAction) {
            ArrayList<Cube> validSerialFromPlayersCube = createValidSerialFromPlayerCube(currentPlayerCubes);

            if (validSerialFromPlayersCube != null) {
                actions.add(createAction(ActionType.CREATE_SERIAL, validSerialFromPlayersCube, null, serialNumber));
                removeCubesFromCurrentPlayerCubes(currentPlayerCubes, validSerialFromPlayersCube);
            }
            else if (canUseBoard) {
                Action insertAction = checkIfCanInsertCubeIntoBoard(currentPlayerCubes, boardCubes);
                
                if (insertAction != null) {
                    if (checkIfNotAlreadyAskToInsertSameCube(insertAction, actions)) {
                        actions.add(insertAction);
                        removeCubesFromCurrentPlayerCubes(currentPlayerCubes, insertAction.getPlayerCubes());
                    }
                    else {
                        removeCubesFromCurrentPlayerCubes(currentPlayerCubes, insertAction.getPlayerCubes());
                    }
                }
                else {
                    hasAnotherAction = false;
                }
            }
            else {
                if (!firstAction) {
                    actions.clear();
                    firstAction = true;
                }
                
                hasAnotherAction = false;
            }
            
            if (!hasAnotherAction && firstAction) {
                actions.add(createAction(ActionType.TAKE_FROM_BANK, null, null, serialNumber));
            }
                
            firstAction = false;
        }
        
        if (actions != null) {
            doSplitInTheEndOfTurn(actions);
        }
        
        return actions;
    }

    private void removeCubesFromCurrentPlayerCubes(ArrayList<Cube> currentPlayerCubes, ArrayList<Cube> serial) {
        for (Cube cube : serial) {
            sumOfFirstDown += cube.getValue();
            currentPlayerCubes.remove(cube);
        }
        
        if (sumOfFirstDown >= 30) {
            canUseBoard = true;
        }
    }

    private Action checkIfCanInsertCubeIntoBoard(ArrayList<Cube> currentPlayerCubes, List<ArrayList<Cube>> boardCubes) {
        ActionType actionType;
        ArrayList<Cube> playerCube = new ArrayList<Cube>();
        int serialNumber = 1;
        Action newAction = null;
        
        for(ArrayList<Cube> serial : boardCubes) {
            for (Cube cube : currentPlayerCubes) {
                if (!cube.isJoker()) {
                    if (checkIfCubeCanInsertIntoSameValueSerial(cube, serial)) {
                        playerCube.add(cube);
                        newAction = createAction(ActionType.ADD_TO_BOTTOM_EDGE, playerCube, null, serialNumber);
                        break;
                    } else if (checkIfCubeCanInsertToEndOfSerial(cube, serial)) {
                        playerCube.add(cube);
                        newAction = createAction(ActionType.ADD_TO_TOP_EDGE, playerCube, null, serialNumber);
                        break;
                    } else if (checkIfCubeCanInsertToBeginOfSerial(cube, serial)) {
                        playerCube.add(cube);
                        newAction = createAction(ActionType.ADD_TO_BOTTOM_EDGE, playerCube, null, serialNumber);
                        break;
                    } else if (checkIfCubeCanInsertToMidOfSerial(cube, serial)) {
                        playerCube.add(cube);
                        newAction = createAction(ActionType.SPLIT, playerCube, null, serialNumber);
                        break;
                    }
                }
            }
            
            serialNumber ++;
            
            if (newAction != null) {
                break;
            }
            else {
                playerCube.clear();
            }
        }
        
        return newAction;
    }

    private boolean checkIfCubeCanInsertIntoSameValueSerial(Cube playerCube, ArrayList<Cube> serial) {
        int lastIndex, firstIndex = 0;
        boolean canInsertIntoSerial = false;
        boolean sameColorAppeared = false;

        lastIndex = serial.size() - 1;
        if (serial.size() <= 3) {
            boolean isCubeWithSameValue = (playerCube.getValue() == serial.get(firstIndex).getValue() && playerCube.getValue() == serial.get(lastIndex).getValue());
            if (isCubeWithSameValue) {
                for (int i = 0; i < serial.size() && !sameColorAppeared; i++) {
                    if (serial.get(i).getColor() == playerCube.getColor()) {
                        sameColorAppeared = true;
                    }
                }
                
                if (!sameColorAppeared) {
                    canInsertIntoSerial = true;
                }
            }
        }

        return canInsertIntoSerial;
    }

    private boolean checkIfCubeCanInsertToEndOfSerial(Cube cube, ArrayList<Cube> serial) {
        boolean canInsertToEnd = false;
        int lastIndex = serial.size() - 1;
        
        if (cube.getColor() == serial.get(lastIndex).getColor() && cube.getColor() == serial.get(0).getColor()) {
            if (serial.get(lastIndex).getValue() + 1 == cube.getValue()) {
                canInsertToEnd = true;
            }
            else if (serial.get(lastIndex).getValue() == 13 && cube.getValue() == 1) {
                canInsertToEnd = true;
            }
        }
        
        return canInsertToEnd;
    }

    private boolean checkIfCubeCanInsertToBeginOfSerial(Cube cube, ArrayList<Cube> serial) {
        boolean canInsertToBegin = false;
        int lastIndex = serial.size() - 1;
        
        if (cube.getColor() == serial.get(lastIndex).getColor() && cube.getColor() == serial.get(0).getColor()) {
            if (serial.get(0).getValue() - 1 == cube.getValue()) {
                canInsertToBegin = true;
            }
            else if (serial.get(0).getValue() == 1 && cube.getValue() == 13) {
                canInsertToBegin = true;
            }
        }
        
        
        return canInsertToBegin;
    }

    private boolean checkIfCubeCanInsertToMidOfSerial(Cube cube, ArrayList<Cube> serial) {
        boolean canInsertToEnd = false;
        int lastIndexCanSplit = serial.size() - 3;
        int firstIndexCanSplit = 2;
        int lastIndex = serial.size() - 1;
        
        if (cube.getColor() == serial.get(lastIndex).getColor() && cube.getColor() == serial.get(0).getColor()) {
            if (serial.size() > 5) {
                if (serial.get(firstIndexCanSplit).getValue() <= cube.getValue() && serial.get(lastIndexCanSplit).getValue() >= cube.getValue()) {
                    canInsertToEnd = true;
                } else if (serial.get(firstIndexCanSplit).getValue() > serial.get(lastIndexCanSplit).getValue()) {
                    if (serial.get(firstIndexCanSplit).getValue() <= cube.getValue()|| 
                            serial.get(lastIndexCanSplit).getValue() >= cube.getValue()) {
                        canInsertToEnd = true;
                    }
                }
            }
        }
        
        
        return canInsertToEnd;
    }

    private boolean checkIfNotAlreadyAskToInsertSameCube(Action insertAction, ArrayList<Action> actions) {
        boolean notAlreadyBeenAsked = true;
        Cube cubeToInsert = insertAction.getPlayerCubes().get(0);
        
        for (Action action : actions) {
            if (action.getActionType() == insertAction.getActionType() && action.getSerialNumber() == insertAction.getSerialNumber()) {
                if (insertAction.getActionType() == ActionType.SPLIT) {
                    notAlreadyBeenAsked = false;
                }
                else {
                    Cube cubeFromAction = action.getPlayerCubes().get(0);
                    if (cubeFromAction.equals(cubeToInsert)) {
                        notAlreadyBeenAsked = false;
                        break;
                    }
                }
            }
        }
        
        return notAlreadyBeenAsked;
    }

    private void doSplitInTheEndOfTurn(ArrayList<Action> actions) {
        Action splitAction = null;
        for (Action action : actions) {
            if (action.getActionType() == ActionType.SPLIT) {
                splitAction = action;
                break;
            }
        }
        
        if (splitAction != null) {
            actions.remove(splitAction);
            actions.add(splitAction);
        }
    }
}
