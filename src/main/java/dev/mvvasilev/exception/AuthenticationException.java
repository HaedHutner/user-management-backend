package dev.mvvasilev.exception;

public class AuthenticationException extends ValidationException {
    public AuthenticationException(String message) {
        super(message);
    }
}
