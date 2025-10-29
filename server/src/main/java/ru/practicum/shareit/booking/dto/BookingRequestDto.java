package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * DTO для запроса на создание нового бронирования.
 * <p>
 * Используется исключительно для приема данных от клиента при создании бронирования.
 * Содержит минимальный набор полей, необходимых для создания бронирования.
 * Отличается от {@link BookingDto} отсутствием служебных полей (id, status, booker, item).
 * </p>
 *
 * <p><b>Контекст использования:</b></p>
 * <ul>
 *   <li>POST запросы на создание бронирования</li>
 *   <li>Валидация входных данных от клиента</li>
 *   <li>Преобразование в полную сущность Booking</li>
 * </ul>
 *
 * <p><b>Особенности:</b></p>
 * <ul>
 *   <li>Не содержит идентификатора (генерируется на сервере)</li>
 *   <li>Не содержит статуса (по умолчанию устанавливается WAITING)</li>
 *   <li>Содержит только itemId вместо полного объекта Item</li>
 *   <li>Booker определяется из контекста аутентификации</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see BookingDto
 * @see ru.practicum.shareit.booking.model.Booking
 * @see ru.practicum.shareit.booking.BookingController
 * @since 2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDto {
    private Integer itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
