package fileManager;

import generated.*;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import rummikubLogic.ExtensionMethods;
import rummikubLogic.RummikubGameLogic;


public class LoadSavedGame {
    private static final int MAX_VALUE_OF_CUBES_WITH_SAME_DATA = 2;
    private static final int MIN_LEN_OF_SERIAL = 2;
    private static final int MAX_LEN_OF_SERIAL = 13;
    private static final int MAX_VALUE_FOR_TILE = 13;
    private static final int MIN_VALUE_FOR_TILE = 1;
    private static final int NUMBER_OF_TILES_GIVEN_TO_PLAYER_WHEN_INIT = 14;
    
    public static EnumsForFile.FileLoadResults checkFileAndInitialize(String filePath, RummikubGameLogic game) {
        EnumsForFile.FileLoadResults result;
        generated.Rummikub remmikub = null;

        try{
            remmikub = loadFromSavedXMLFile(filePath);
        }
        catch (Exception exception){
            result = EnumsForFile.FileLoadResults.FILE_NOT_FOUND;
        }
        
        if (remmikub != null) {
            if (checkFileValidation(remmikub, game)) {
                result = EnumsForFile.FileLoadResults.FILE_VALID;
                initSavedGame(remmikub, game);
            }
            else {
                result = EnumsForFile.FileLoadResults.FILE_NOT_VALID;
            }
        }
        else {
            result = EnumsForFile.FileLoadResults.FILE_NOT_FOUND;
        }
        
        return result;
    }
    
    public static generated.Rummikub loadFromSavedXMLFile(String filePath) throws Exception {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        ClassLoader classLoader = LoadSavedGame.class.getClassLoader();
        URL resource = classLoader.getResource("Resource/rummikub.xsd");
        Schema schema = sf.newSchema(resource);
        
        JAXBContext context = JAXBContext.newInstance(Rummikub.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setSchema(schema);
        return (Rummikub)unmarshaller.unmarshal(new File(filePath));
    }    
    
    private static void initSavedGame(Rummikub rummikub, RummikubGameLogic game) {
        game.initGameFromLoadedFile(rummikub);
    }
    
    public static boolean checkFileValidation(Rummikub rummikub, RummikubGameLogic game) {
        List<Players.Player> gamePlayers = rummikub.getPlayers().getPlayer(); 
        String currentPlayerName = rummikub.getCurrentPlayer();
        Board gameBoard = rummikub.getBoard();
        
        boolean validFile = checkIfCurrentPlayerExistInPlayers(gamePlayers, currentPlayerName) &&
                checkValidationOfCubes(gameBoard, gamePlayers) &&
                checkValidationOfBoard(gameBoard, game) &&
                checkIfPlayersNumberOfTilesIsValid(gameBoard, gamePlayers);
        
        return validFile;
    }
    
    private static boolean checkIfCurrentPlayerExistInPlayers(List<Players.Player> gamePlayers, String currentPlayerName) {
        boolean currentPlayerExist = false;
        
        for (Players.Player gamePlayer : gamePlayers)  {
            if (currentPlayerName.equals(gamePlayer.getName())) {
                currentPlayerExist = true;
                break;
            }
        }
        
        return currentPlayerExist;
    }

    private static boolean checkValidationOfCubes(Board gameBoard, List<Players.Player> allPlayers) {
        int[] blackTiles = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] blueTiles = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] yellowTiles = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] redTiles = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int numOfJokers = 0;
        
        for (Board.Sequence serial : gameBoard.getSequence()) {
            for (Tile tile : serial.getTile()) {
                if (tile.getValue() == 0) {
                    numOfJokers++;
                    if (numOfJokers > MAX_VALUE_OF_CUBES_WITH_SAME_DATA) {
                        return false;
                    }
                }
                else {
                    switch (tile.getColor()) {
                        case BLUE:
                            blueTiles[tile.getValue() - 1]++;
                            if (blueTiles[tile.getValue() - 1] > MAX_VALUE_OF_CUBES_WITH_SAME_DATA) {
                                return false;
                            }
                            break;
                        case BLACK:
                            blackTiles[tile.getValue() - 1]++;
                            if (blackTiles[tile.getValue() - 1] > MAX_VALUE_OF_CUBES_WITH_SAME_DATA) {
                                return false;
                            }
                            break;
                        case YELLOW:
                            yellowTiles[tile.getValue() - 1]++;
                            if (yellowTiles[tile.getValue() - 1] > MAX_VALUE_OF_CUBES_WITH_SAME_DATA) {
                                return false;
                            }
                            break;
                        case RED:
                            redTiles[tile.getValue() - 1]++;
                            if (redTiles[tile.getValue() - 1] > MAX_VALUE_OF_CUBES_WITH_SAME_DATA) {
                                return false;
                            }
                            break;
                    }
                }
            }
        }
        
        for (Players.Player player : allPlayers) {
            for (Tile tile : player.getTiles().getTile()) {
                if (tile.getValue() == 0) {
                    numOfJokers++;
                    if (numOfJokers > MAX_VALUE_OF_CUBES_WITH_SAME_DATA) {
                        return false;
                    }
                }
                else {
                    switch (tile.getColor()) {
                        case BLUE:
                            blueTiles[tile.getValue() - 1]++;
                            if (blueTiles[tile.getValue() - 1] > MAX_VALUE_OF_CUBES_WITH_SAME_DATA) {
                                return false;
                            }
                            break;
                        case BLACK:
                            blackTiles[tile.getValue() - 1]++;
                            if (blackTiles[tile.getValue() - 1] > MAX_VALUE_OF_CUBES_WITH_SAME_DATA) {
                                return false;
                            }
                            break;
                        case YELLOW:
                            yellowTiles[tile.getValue() - 1]++;
                            if (yellowTiles[tile.getValue() - 1] > MAX_VALUE_OF_CUBES_WITH_SAME_DATA) {
                                return false;
                            }
                            break;
                        case RED:
                            redTiles[tile.getValue() - 1]++;
                            if (redTiles[tile.getValue() - 1] > MAX_VALUE_OF_CUBES_WITH_SAME_DATA) {
                                return false;
                            }
                            break;
                    }
                }
            }
        }
        
        return true;
    }

    private static boolean checkValidationOfBoard(Board gameBoard, RummikubGameLogic game) {
        return game.checkValidBoardFromLoadedFile(ExtensionMethods.replaceListOfTilesIntoListOfCubes(gameBoard.getSequence()));
    }
    
    private static int compareTiles(Tile tile1, Tile tile2) {
        return tile1.getValue() - tile2.getValue();
    }

    private static boolean checkCurrentSerial(List<Tile> serial) {
        int indexOfFirstTile = 0;
        boolean res = true;
        
        Tile firstTile = serial.get(indexOfFirstTile);
        Tile secondTile = serial.get(indexOfFirstTile + 1);
        
        if (!secondTile.equals(firstTile)) {
            if (secondTile.getColor() != firstTile.getColor()) {
                res = checkSameValueSerial(serial, firstTile.getValue());
            }
            else {
                res = checkSameColorSerial(serial, firstTile.getColor(), firstTile.getValue());
            }
        }
        
        return res;
    }

    private static boolean checkSameValueSerial(List<Tile> serial, int serialValue) {
        boolean isValid = true;
        boolean blackAppeared = false;
        boolean yellowAppeared = false;
        boolean blueAppeared = false;
        boolean redAppeared = false;
        
        for (Tile tile : serial) {
            if (tile.getValue() != 0) {
                if (tile.getValue() != serialValue) {
                    isValid = false;
                    break;
                } else {
                    generated.Color colorOfCube = tile.getColor();
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
            }
        }
        
        return isValid;
    }

    private static boolean checkSameColorSerial(List<Tile> serial, Color serialColor, int firstTileValue) {
        boolean isValid = true;
        int lastTileCheckedValue = firstTileValue;
        Tile firstTile = serial.get(0);
        
        for (Tile tile : serial) {
            if (tile != firstTile) {
                if (tile.getColor() != serialColor) {
                    isValid = false;
                    break;
                }
                else {
                    if (tile.getValue() == lastTileCheckedValue + 1) {
                        lastTileCheckedValue = tile.getValue();
                    }
                    else {
                        if (tile.getValue() == MIN_VALUE_FOR_TILE && lastTileCheckedValue == MAX_VALUE_FOR_TILE) {
                            lastTileCheckedValue = tile.getValue();
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
   
    private static boolean checkIfPlayersNumberOfTilesIsValid(Board gameBoard, List<Players.Player> gamePlayers) {
        int numberOfPlayers = gamePlayers.size();
        int[] numberOfTilesOfEachPlayer = new int[numberOfPlayers];
        int numberOfTilesFromBoard = 0;
        boolean res;
        int index = 0;
        
        for(Players.Player player : gamePlayers) {
            numberOfTilesOfEachPlayer[index] = player.getTiles().getTile().size();
            index++;
        }
        
        for (Board.Sequence sequence : gameBoard.getSequence()) {
            numberOfTilesFromBoard += sequence.getTile().size();
        }
        
        for (int i = 0; i < numberOfPlayers; i++) {
            if (numberOfTilesOfEachPlayer[i] < NUMBER_OF_TILES_GIVEN_TO_PLAYER_WHEN_INIT) {
                numberOfTilesFromBoard -= (NUMBER_OF_TILES_GIVEN_TO_PLAYER_WHEN_INIT - numberOfTilesOfEachPlayer[i]);
            }
        }
        
        if (numberOfTilesFromBoard < 0) {
            res = false;
        }
        else {
            res = true;
        }
        
        return res;
    }

    private static int getNumberOfDigitalPlayer(List<Players.Player> gamePlayers) {
        int res = 0;
        
        for (Players.Player gamePlayer : gamePlayers) {
            if (gamePlayer.getType() == PlayerType.COMPUTER) {
                res++;
            }
        }
        
        return res;
    }

    private static ArrayList<String> getNameOfPlayers(List<Players.Player> gamePlayers) {
        ArrayList<String> res = new ArrayList<String>();
        
        for (Players.Player player : gamePlayers) {
            res.add(player.getName());
        }
        
        return res;
    }

    private static ArrayList<Boolean> getPlayerCanUseBoardList(List<Players.Player> gamePlayers) {
        ArrayList<Boolean> res = new ArrayList<Boolean>();
        for(Players.Player player : gamePlayers) {
            res.add(player.isPlacedFirstSequence());
        }
        
        return res;
    }
}