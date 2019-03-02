package fileManager;

import rummikubLogic.*;
import rummikubLogic.Board;
import generated.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;
import dataObject.Cube;

public abstract class SaveGameManager
{
    public static String lastFilePath;
    
    public static EnumsForFile.FileSaveResults saveGame(RummikubGameLogic game) {
        boolean succeedToSave;
        generated.Rummikub rummikub = preparingToSave(game);
        succeedToSave = handlingSaving(rummikub, lastFilePath);
        
        return succeedToSave ? EnumsForFile.FileSaveResults.FILE_SAVED : EnumsForFile.FileSaveResults.FILE_COULD_NOT_SAVED;
    }
    
    public static EnumsForFile.FileSaveResults saveAsGame(RummikubGameLogic game, String dir, String fileName) {
        generated.Rummikub rummikub = preparingToSave(game);
        boolean fileNotExist = false;

        if (!checkIfFolderExists(dir)) {
            return EnumsForFile.FileSaveResults.FOLDER_NOT_FOUND;
        }

        if (!checkIfFileHasXmlExtension(fileName)) {
            return EnumsForFile.FileSaveResults.FILE_WITHOUT_XML_EXESTION;
        }
        
        String seperate = dir.charAt(dir.length() - 1) == '\\' ? "" : "\\";
        
        return doSaveFileAction(rummikub, game, dir + seperate + fileName);
    }
    
    public static EnumsForFile.FileSaveResults saveAsGame(RummikubGameLogic game, String xmlPath) {
        generated.Rummikub rummikub = preparingToSave(game);
        return doSaveFileAction(rummikub, game, xmlPath);
    }
    
    private static EnumsForFile.FileSaveResults doSaveFileAction(generated.Rummikub rummikub, RummikubGameLogic game, String xmlPath) {        
        boolean succeedToSave = handlingSaving(rummikub, xmlPath);
        
        return succeedToSave ? EnumsForFile.FileSaveResults.FILE_SAVED : EnumsForFile.FileSaveResults.FILE_COULD_NOT_SAVED;
    }
    
    private static boolean checkIfFileExists(String filePath) {
        File fileToSave;
        try { 
            fileToSave = new File(filePath);
        }
        catch (Exception ex){
            return false;
        }

        return fileToSave.exists();
    }
    
    private static boolean checkIfFolderExists(String filePath) {
        File folder;
        
        try {
            folder = new File(filePath);
        }
        catch (Exception ex) {
            return false;
        }
        
        return folder.isDirectory();
    }
    
    private static boolean checkIfFileHasXmlExtension(String fileName) {
        String extension;
        
        
        try{
            extension = fileName.substring(fileName.lastIndexOf("."));
        }
        catch (StringIndexOutOfBoundsException exception){
            extension = ""; 
        }

        return extension.equals(".xml");
    }

    private static boolean handlingSaving(Rummikub rummikub, String filePath) {
        boolean succeedToSave;
        
        try{
            createSavedXMLFile(filePath, rummikub);
            succeedToSave = true;
            lastFilePath = filePath;
        }
        catch (JAXBException | SAXException exception){
            succeedToSave = false;
        }
        
        return succeedToSave;
    }
    
    private static void createSavedXMLFile(String filePath, Rummikub rummikub) throws JAXBException, SAXException {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); 
        Schema schema = sf.newSchema(new File("Resource\\rummikub.xsd")); 
        
        JAXBContext jaxbContext = JAXBContext.newInstance(Rummikub.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setSchema(schema);
        File file = new File(filePath);
        marshaller.marshal(rummikub, file); 
    }
    
    private static generated.Rummikub preparingToSave(RummikubGameLogic game) {
        ObjectFactory objectFactory = new ObjectFactory();
        Rummikub rummikub = objectFactory.createRummikub();
        
        rummikub.setCurrentPlayer(game.getCurrentPlayerName());
        rummikub.setName(game.getGameName());
        
        generated.Players gamePlayers = saveTheGamePlayers(game.getPlayers());
        rummikub.setPlayers(gamePlayers);
        
        generated.Board boardToSave = saveTheBoard(game.getGameBoard());
        rummikub.setBoard(boardToSave);
        
        return rummikub;
    }
    
    private static generated.Players saveTheGamePlayers(List<Player> players) {
        ObjectFactory objectFactory = new ObjectFactory();
        generated.Players gamePlayers = objectFactory.createPlayers();
        List<Players.Player> playersToSave = gamePlayers.getPlayer();
        
        for (rummikubLogic.Player player : players) {
            playersToSave.add(prepareToSavePlayer(player));
        }
        
        return gamePlayers;
    }
    
    private static generated.Players.Player prepareToSavePlayer(Player logicPlayer) {
        ObjectFactory objectFactory = new ObjectFactory();
        generated.Players.Player playerToSave = objectFactory.createPlayersPlayer();
        
        playerToSave.setName(logicPlayer.getName());
        PlayerType type = logicPlayer.isComputer()? PlayerType.COMPUTER : PlayerType.HUMAN;
        playerToSave.setType(type);
        
        Players.Player.Tiles tiles = objectFactory.createPlayersPlayerTiles();
        List<Tile> listOfTiles = prepareListOfTiles(logicPlayer.getCubes());
        
        tiles.getTile().addAll(listOfTiles);
        playerToSave.setTiles(tiles);
        
        playerToSave.setPlacedFirstSequence(logicPlayer.isPlayerCanUseBoard());
        
        return playerToSave;
    }
    
    private static generated.Board saveTheBoard(Board gameBoard) {
        ObjectFactory objectFactory = new ObjectFactory();
        generated.Board boardToSave = objectFactory.createBoard();
        List<generated.Board.Sequence> allSequenceOfBoard  = boardToSave.getSequence();
        List<ArrayList<Cube>> listOfAllSerial = gameBoard.getCubes();
        
        for (ArrayList<Cube> serial : listOfAllSerial) {
            generated.Board.Sequence sequence = objectFactory.createBoardSequence();
            sequence.getTile().addAll(prepareListOfTiles(serial));
            allSequenceOfBoard.add(sequence);
        }
        
        return boardToSave;
    }

    private static List<Tile> prepareListOfTiles(List<Cube> cubes) {
        ObjectFactory objectFactory = new ObjectFactory();
        List<Tile> listOfTiles = new ArrayList<Tile>();
        Tile currentTile;
        generated.Color currentColor;
        
        for (Cube cube : cubes) {
            currentTile = objectFactory.createTile();
            currentColor = generated.Color.valueOf(cube.getColor().name());
            currentTile.setColor(currentColor);
            
            if (cube.isJoker()) {
                currentTile.setValue(0);
            }
            else {
                currentTile.setValue(cube.getValue());
            }
            
            listOfTiles.add(currentTile);
        }
        
        return listOfTiles;
    }
}