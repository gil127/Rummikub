
package fileManager;

public class EnumsForFile {

    public enum FileLoadResults {
        DUPLICATE_GAME_NAME, INVALID_PARAMETERS, INVALID_XML, FILE_NOT_FOUND, FILE_NOT_VALID, FILE_VALID;
    }

    public enum FileSaveResults {
        FOLDER_NOT_FOUND, FILE_WITHOUT_XML_EXESTION, FILE_SAVED, FILE_COULD_NOT_SAVED;
    }
}