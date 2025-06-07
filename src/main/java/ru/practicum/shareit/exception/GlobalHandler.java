package ru.practicum.shareit.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleValidationExceptionNotFound(NotFoundException e) {
        return ErrorResponse.builder().message("Ошибка валидации").details(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptionBadRequest(BadRequestException e) {
        return ErrorResponse.builder().message("Ошибка валидации").details(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleValidationDuplicateEmail(DuplicateEmailException e) {
        return ErrorResponse.builder().message("Ошибка валидации").details(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationMethodExceptions(MethodArgumentNotValidException e) {
        StringBuilder errorMessage = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            errorMessage.append(fieldError.getField())
                    .append(": ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        });
        log.error("Ошибка валидации: {}", errorMessage);
        return ErrorResponse.builder().message("Ошибка валидации").details(errorMessage.toString()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintException(ConstraintViolationException e) {
        StringBuilder errorMessage = new StringBuilder();
        e.getConstraintViolations().forEach(violation -> {
            errorMessage.append(violation.getPropertyPath())
                    .append(": ")
                    .append(violation.getMessage())
                    .append("; ");
        });
        log.error("Ошибка валидации: {}", errorMessage);
        return ErrorResponse.builder().message("Ошибка валидации").details(errorMessage.toString()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        String parameterName = e.getName();
        String parameterValue = String.valueOf(e.getValue());
        String errorMessage = String.format("Передано некорректное значение параметра %s: %s",
                parameterName, parameterValue);
        log.error(errorMessage);
        return ErrorResponse.builder().message(errorMessage).details(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnknown(Exception e, HttpServletRequest request) {
        log.error("Произошло неизвестное исключение при запросе с методом: {}," +
                "адрес запроса: {},   с ошибкой: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return ErrorResponse.builder().message("Произошло неизвестное исключение, проверьте данные запроса")
                .details(e.getMessage()).build();
    }
}
