package converter;

import dataObject.Cube;
import fileManager.EnumsForFile;
import java.util.ArrayList;
import java.util.List;
import ws.rummikub.Color;
import ws.rummikub.DuplicateGameName_Exception;
import ws.rummikub.InvalidParameters_Exception;
import ws.rummikub.Tile;

public class Converter {
    public static EnumsForFile.FileLoadResults convertExceptionToEnumsOfFile(Exception ex) {
        EnumsForFile.FileLoadResults result;
        
        if (ex instanceof DuplicateGameName_Exception) {
            result = EnumsForFile.FileLoadResults.DUPLICATE_GAME_NAME;
        }
        else if (ex instanceof InvalidParameters_Exception ) {
            result = EnumsForFile.FileLoadResults.INVALID_PARAMETERS;
        }
        else {
            result = EnumsForFile.FileLoadResults.INVALID_XML;
        }
        
        return result;
    }
    
    public static List<Cube> convertListOfTilesToListOfCubes(List<Tile> tiles) {
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

    public static Cube convertTileIntoCube(Tile tile) {
        int value = tile.getValue();
        Cube.Color color = convertGeneratedColorToLogicColor(tile.getColor());
        boolean isJoker = value == 0;
        
        return new Cube(color, value, isJoker);
    }

    public static Tile convertCubeIntoTile(Cube cube) {
        Tile tile = new Tile();
        tile.setValue(cube.getValue());
        tile.setColor(convertLogicColorToGeneratedColor(cube.getColor()));
        
        return tile;
    }

    private static Color convertLogicColorToGeneratedColor(Cube.Color color) {
        Color generatedColor;
        
        switch (color) {
            case BLACK : generatedColor = Color.BLACK;
                break;
            case BLUE : generatedColor = Color.BLUE;
                break;
            case RED : generatedColor = Color.RED;
                break;
            default: generatedColor = Color.YELLOW;
                break;
        }
        
        return generatedColor;
    }

    public static List<Tile> convertListOfCubesToListOfTiles(ArrayList<Cube> cubes) {
        List<Tile> tiles = new ArrayList<Tile>();
        for (Cube cube : cubes) {
            Tile tile = convertCubeIntoTile(cube);
            tiles.add(tile);
        }
        
        return tiles;
    }
}
