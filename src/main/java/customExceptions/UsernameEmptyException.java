package customExceptions;

public class UsernameEmptyException extends Exception {
    public UsernameEmptyException(String message) {
        super(message);
    }
}
