package database;

public class DatabaseException extends RuntimeException {
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);   // stores the cause for getCause()
    }

    public DatabaseException(String message) {
        super(message);
    }
}
