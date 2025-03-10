package dataaccess;

public class InvalidAccessException extends DataAccessException {
  public InvalidAccessException(String message) {
    super(message);
  }
}
