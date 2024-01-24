package utils.customExceptions;

/**
 * This exception is thrown when the username is already used
 */
public class UsernameUsedException extends Exception{
    public UsernameUsedException(String message) {
        super(message);
    }
}
