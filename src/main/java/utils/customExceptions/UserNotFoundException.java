package utils.customExceptions;

/**
 * This exception is thrown when the user is not found
 */
public class UserNotFoundException extends Exception{
    public UserNotFoundException(String message) {
        super(message);
    }
}
