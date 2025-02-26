package exceptions;
/**
 * BadCommandExeption is a runtime exception used in Shell class
 * because a bad input of the user
 */
public class BadCommandExeption extends RuntimeException{
    /**
     * Constructs a BadCommandExeption with an error message.
     *
     * @param message the message to display.
     */
    public BadCommandExeption(String message){
        super(message);
    }
}
