package exceptions;

public class DuplicateNameException extends RuntimeException{

    public DuplicateNameException() {
	super ("Name already exists");
    }
}
