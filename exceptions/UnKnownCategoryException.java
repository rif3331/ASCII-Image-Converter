package exceptions;

/**
 * UnKnownCategoryException is a subclass of BadCommandExeption, that take care
 * about errors about unknown categorys.
 */
public class UnKnownCategoryException extends BadCommandExeption{
    /**
     * Constructs a UnKnownCategoryException with an error message.
     *
     * @param message the message to display.
     */
    public UnKnownCategoryException(String message) {
        super(message);
    }
}
