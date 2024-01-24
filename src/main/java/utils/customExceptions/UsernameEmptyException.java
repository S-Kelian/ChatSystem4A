package utils.customExceptions;

/**
 * This exception is thrown when the username is empty
 */
public class UsernameEmptyException extends Exception {
    public UsernameEmptyException(String message) {
        super(message);
    }
}