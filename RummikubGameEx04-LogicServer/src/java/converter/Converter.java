/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package converter;

import dataObject.Action;
import dataObject.ActionType;
import dataObject.Cube;
import dataObject.CubeBoard;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import rummikubLogic.Player;
import rummikubLogic.RummikubGameLogic;
import ws.rummikub.Color;
import ws.rummikub.GameDetails;
import ws.rummikub.GameStatus;
import ws.rummikub.PlayerDetails;
import ws.rummikub.PlayerStatus;
import ws.rummikub.PlayerType;
import ws.rummikub.Tile;

/**
 *
 * @author or
 */
public class Converter {
    public static List<PlayerDetails> convertPlayersToPlayersDetails(List<Player> players) {
        List<PlayerDetails> res = new ArrayList<PlayerDetails>();
        
        for (Player player : players) {
            PlayerDetails playerDetails = convertPlayerToPlayerDetails(player, false);
            
            res.add(playerDetails);
        }
        
        return res;
    }

    public static PlayerDetails convertPlayerToPlayerDetails(Player player, boolean needToAddTiles) {
        PlayerDetails res = new PlayerDetails();
        
        res.setName(player.getName());
        res.setNumberOfTiles(player.getNumerOfCubes());
        res.setPlayedFirstSequence(player.isPlayerCanUseBoard());
        res.setStatus(convertStatus(player.getStatus()));
        res.setType(convertPlayerType(player.isComputer()));
        if (needToAddTiles) {
            res.getTiles().addAll(convertListOfCubesToListOfTiles(player.getCubes()));
        }
        
        return res;
    }

    private static PlayerStatus convertStatus(Player.PlayerStatus status) {
        PlayerStatus res;
        
        switch (status) {
            case ACTIVE: res = PlayerStatus.ACTIVE;
                break;
            case Wait: res = PlayerStatus.ACTIVE;
                break;
            case JOINED: res = PlayerStatus.JOINED;
                break;
            default: res = PlayerStatus.RETIRED;
                break;
        }
        
        return res;
    }

    private static PlayerType convertPlayerType(boolean isComputer) {
        PlayerType type;
        
        if (isComputer) {
            type = PlayerType.COMPUTER;
        }
        else {
            type = PlayerType.HUMAN;
        }
        
        return type;
    }

    public static GameDetails convertGameToGameDetails(RummikubGameLogic game) {
        GameDetails res = new GameDetails();
        
        res.setComputerizedPlayers(game.getNumberOfComputerPlayers());
        res.setHumanPlayers(game.getNumberOfHumanPlayers());
        res.setJoinedHumanPlayers(game.getNumberOfPlayersJoinedToGame());
        res.setLoadedFromXML(game.isLoadedFromXML());
        res.setName(game.getGameName());
        res.setStatus(convertGameStatus(game.getGameStatus()));
        
        return res;
    }

    private static GameStatus convertGameStatus(RummikubGameLogic.GameStatus gameStatus) {
        GameStatus res;
        
        switch (gameStatus) {
            case ACTIVE: res = GameStatus.ACTIVE;
                break;
            case FINISHED: res = GameStatus.FINISHED;
                break;
            default: res = GameStatus.WAITING;
                break;
        }
        
        return res;
    }

    public static List<Tile> convertListOfCubesToListOfTiles(List<Cube> cubes) {
        List<Tile> listOfTiles = new ArrayList<Tile>();
        
        for(Cube cube : cubes) {
            Tile tile = convertCubeToTile(cube);
            listOfTiles.add(tile);
        }
        
        return listOfTiles;
    }

    public static Tile convertCubeToTile(Cube cube) {
        Tile tile = new Tile();
        
        tile.setColor(convertLogicColorToGeneratedColor(cube.getColor()));
        
        if (cube.isJoker()) {
            tile.setValue(0);
        }
        else {
            tile.setValue(cube.getValue());
        }
        
        return tile;
    }

    private static Color convertLogicColorToGeneratedColor(Cube.Color color) {
        Color generatedColor;
        
        switch(color) {
            case BLACK: generatedColor = Color.BLACK;
                break;
            case BLUE: generatedColor = Color.BLUE;
                break;
            case RED: generatedColor = Color.RED;
                break;
            default: generatedColor = Color.YELLOW;
                break;
        }
        
        return generatedColor;
    }

    public static Action makeCreateSequenceAction(List<Tile> tiles) {
        Action action = new Action();
        List<Cube> cubes = new ArrayList<Cube>();
        
        cubes = convertListOfTilesToListOfCubes(tiles);
        action.setActionType(ActionType.CREATE_SERIAL);
        action.getPlayerCubes().addAll(cubes);
        
        return action;
    }

    private static List<Cube> convertListOfTilesToListOfCubes(List<Tile> tiles) {
        List<Cube> cubes = new ArrayList<Cube>();
        
        for (Tile tile : tiles) {
            Cube cube = convertTileIntoCube(tile);
            
            cubes.add(cube);
        }
        
        return cubes;
    }

    private static Cube.Color convertGeneratedColorToLogicColor(Color color) {
        Cube.Color logicColor;
        
        switch (color) {
            case BLACK : logicColor = Cube.Color.BLACK;
                break;
            case BLUE : logicColor = Cube.Color.BLUE;
                break;
            case RED : logicColor = Cube.Color.RED;
                break;
            default: logicColor = Cube.Color.YELLOW;
                break;
        }
        
        return logicColor;
    }

    private static Cube convertTileIntoCube(Tile tile) {
        int value = tile.getValue();
        Cube.Color color = convertGeneratedColorToLogicColor(tile.getColor());
        boolean isJoker = value == 0;
        
        return new Cube(color, value, isJoker);
    }

    public static Action makeInsertActionFromAddTileEvent(Tile tile, ArrayList<Cube> serial, int sequencePosition, int sequenceIndex) {
        Action action = new Action();
        Cube cube = convertTileIntoCube(tile);
        action.addPlayerCubes(cube);
        action.setSerialNumber(sequenceIndex);
        
        if (sequencePosition == 0) {
            action.setActionType(ActionType.ADD_TO_BOTTOM_EDGE);
        }
        else if (sequencePosition == serial.size() - 1) {
            action.setActionType(ActionType.ADD_TO_TOP_EDGE);
        }
        else {
            action.setActionType(ActionType.SPLIT);
        }
        
        return action;
    }

    public static Action makeInsertActionFromMoveTileEvent(ArrayList<Cube> sourceSerial, int sourceSequencePosition, int sourceSequenceIndex, 
            ArrayList<Cube> targetSerial, int targetSequencePosition, int targetSequenceIndex) {
        
        Action action = new Action();
        
        action.setSerialNumber(targetSequenceIndex);
        
        if (targetSequencePosition == 0) {
            action.setActionType(ActionType.ADD_TO_BOTTOM_EDGE);
        }
        else if (targetSequencePosition == targetSerial.size() - 1) {
            action.setActionType(ActionType.ADD_TO_TOP_EDGE);
        }
        else {
            action.setActionType(ActionType.SPLIT);
        }
        
        Cube cube = sourceSerial.get(sourceSequencePosition);
        CubeBoard cubeBoard = new CubeBoard(cube, sourceSequenceIndex);
        action.addBoardCube(cubeBoard);
        
        return action;
    }
}
