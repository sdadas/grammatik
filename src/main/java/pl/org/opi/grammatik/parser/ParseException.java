package pl.org.opi.grammatik.parser;

/**
 * @author Sławomir Dadas
 */
public class ParseException extends RuntimeException {

    public ParseException() {
    }

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseException(Throwable cause) {
        super(cause);
    }

    public ParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ParseException(int line, String message) {
        super(String.format("line %d: %s", line, message));
    }

    public ParseException(int line, int col, String message) {
        super(String.format("line %d:%d: %s", line, col, message));
    }
}
