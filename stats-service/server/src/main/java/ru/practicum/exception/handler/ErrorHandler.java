package ru.practicum.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.InvalidDataException;

import java.time.LocalDateTime;

@RestControllerAdvice
@ResponseBody
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidDatesException(InvalidDataException e) {
        return new ErrorResponse("BAD_REQUEST", "Incorrectly made request", e.getMessage(), LocalDateTime.now());
    }
}