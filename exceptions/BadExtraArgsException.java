package exceptions;
/**
 * BadExtraArgsException is a subclass of BadCommandExeption, that take care
 * about errors about user add to many args to the ask.
 */
public class BadExtraArgsException extends BadCommandExeption{
    /**
     * Constructs a BadExtraArgsException with an error message.
     *
     * @param message the message to display.
     */
    public BadExtraArgsException(String message) {
        super(message);
    }
}
