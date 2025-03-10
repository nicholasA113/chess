package dataaccess;

public class InvalidInputException extends DataAccessException {
    public InvalidInputException(String message) {
        super(message);
    }
}
