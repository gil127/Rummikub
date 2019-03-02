package exceptions;

public class TooManyPlayersAddedException extends RuntimeException {

    public TooManyPlayersAddedException(int maxPlayers) {
	super ("You can't add more than " + maxPlayers + " players.");
    }
}
