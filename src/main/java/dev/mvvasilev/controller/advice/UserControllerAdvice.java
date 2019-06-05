package dev.mvvasilev.controller.advice;

import dev.mvvasilev.controller.UserController;
import dev.mvvasilev.dto.ErrorDTO;
import dev.mvvasilev.exception.UserNotFoundException;
import dev.mvvasilev.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Miroslav Vasilev
 */
@RestControllerAdvice(basePackageClasses = UserController.class)
public class UserControllerAdvice {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO userNotFoundException(UserNotFoundException exception) {
        return ErrorDTO.of(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO validationException(ValidationException exception) {
        return ErrorDTO.of(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

}
