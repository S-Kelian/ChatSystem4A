package utils.customExceptions;

/**
 * This exception is thrown when the OS is not supported
 */
public class OsNotSupportedException extends Exception{
    public OsNotSupportedException(String message) {
        super(message);
    }
}
