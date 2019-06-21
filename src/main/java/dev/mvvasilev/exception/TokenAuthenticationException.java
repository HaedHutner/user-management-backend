package dev.mvvasilev.exception;

public class TokenAuthenticationException extends org.springframework.security.core.AuthenticationException {

    public TokenAuthenticationException(String msg) {
        super(msg);
    }

}
