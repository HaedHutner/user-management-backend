package dev.mvvasilev.exception;

/**
 * @author Miroslav Vasilev
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
