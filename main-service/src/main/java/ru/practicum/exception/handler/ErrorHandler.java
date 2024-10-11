package ru.practicum.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.DataConflictException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.exception.ForbiddenException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptionException(MethodArgumentNotValidException e) {
        saveErrorLog(e);
        return new ErrorResponse("BAD_REQUEST", "Argument vot valid", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(BadRequestException e) {
        saveErrorLog(e);
        return new ErrorResponse("BAD_REQUEST", "Incorrectly made request", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(DataNotFoundException e) {
        saveErrorLog(e);
        return new ErrorResponse("NOT_FOUND", "The required object was not found", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataConflictException(DataConflictException e) {
        saveErrorLog(e);
        return new ErrorResponse("CONFLICT", "There is some conflict", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleForbiddenException(ForbiddenException e) {
        saveErrorLog(e);
        return new ErrorResponse("FORBIDDEN", "For the requested operation the conditions are not met", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Throwable e) {
        saveErrorLog(e);
        return new ErrorResponse("INTERNAL_SERVER_ERROR", e.getCause().toString(), e.getMessage(), LocalDateTime.now());
    }

    private void saveErrorLog(Throwable e) {
        log.error("Exception cause: {}, message: {}", e.getCause(), e.getMessage());
    }
}
