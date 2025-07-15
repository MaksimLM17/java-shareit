package ru.practicum.shareit.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalHandlerTest {

    @InjectMocks
    private GlobalHandler globalHandler;

    @Mock
    private HttpServletRequest request;

    @Test
    void handleValidationExceptionNotFound_shouldReturnNotFoundResponse() {
        String errorMessage = "Не найдено";
        NotFoundException exception = new NotFoundException(errorMessage);

        ErrorResponse response = globalHandler.handleValidationExceptionNotFound(exception);

        assertNotNull(response);
        assertEquals("Ошибка валидации", response.getError());
        assertEquals(errorMessage, response.getDetails());
    }

    @Test
    void handleValidationExceptionBadRequest_shouldReturnBadRequestResponse() {
        String errorMessage = "Некорректный запрос";
        BadRequestException exception = new BadRequestException(errorMessage);

        ErrorResponse response = globalHandler.handleValidationExceptionBadRequest(exception);

        assertNotNull(response);
        assertEquals("Ошибка валидации", response.getError());
        assertEquals(errorMessage, response.getDetails());
    }

    @Test
    void handleValidationDuplicateEmail_shouldReturnConflictResponse() {
        String errorMessage = "Email уже существует";
        DuplicateEmailException exception = new DuplicateEmailException(errorMessage);

        ErrorResponse response = globalHandler.handleValidationDuplicateEmail(exception);

        assertNotNull(response);
        assertEquals("Ошибка валидации", response.getError());
        assertEquals(errorMessage, response.getDetails());
    }

    @Test
    void handleUnknown_shouldReturnInternalServerErrorResponse() {
        String errorMessage = "Неизвестная ошибка";
        Exception exception = new Exception(errorMessage);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/items");

        ErrorResponse response = globalHandler.handleUnknown(exception, request);

        assertNotNull(response);
        assertEquals("Произошло неизвестное исключение, проверьте данные запроса", response.getError());
        assertEquals(errorMessage, response.getDetails());

        verify(request).getMethod();
        verify(request).getRequestURI();
    }
}