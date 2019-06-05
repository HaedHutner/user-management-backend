package dev.mvvasilev.dto;

import org.springframework.http.HttpStatus;

/**
 * @author Miroslav Vasilev
 */
public class ErrorDTO {

    private int status;

    private String message;

    public ErrorDTO(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static ErrorDTO of(HttpStatus status, String message) {
        return new ErrorDTO(status.value(), message);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
