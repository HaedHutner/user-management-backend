package dev.mvvasilev.exception;

/**
 * @author Miroslav Vasilev
 */
public class UserNotFoundException extends ValidationException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
