
package fileManager;

public class EnumsForFile {

    public enum FileLoadResults {
        FILE_NOT_FOUND, FILE_NOT_VALID, FILE_VALID;
    }

    public enum FileSaveResults {
        FOLDER_NOT_FOUND, FILE_WITHOUT_XML_EXESTION, FILE_SAVED, FILE_COULD_NOT_SAVED;
    }
}
