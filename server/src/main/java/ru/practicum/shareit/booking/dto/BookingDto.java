package ru.practicum.shareit.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
/**
 * DTO (Data Transfer Object) для передачи данных о бронировании.
 * <p>
 * Используется для обмена данными между клиентом и сервером через REST API.
 * Содержит полную информацию о бронировании, включая связанные сущности (предмет, пользователь).
 * Не содержит бизнес-логики и аннотаций JPA.
 * </p>
 *
 * <p><b>Контекст использования:</b></p>
 * <ul>
 *   <li>Создание нового бронирования (POST запросы)</li>
 *   <li>Получение информации о бронировании (GET запросы)</li>
 *   <li>Отображение списков бронирований</li>
 *   <li>Ответы API с детальной информацией о бронировании</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see ru.practicum.shareit.booking.model.Booking
 * @see ItemDto
 * @see UserDto
 * @see Status
 * @see ru.practicum.shareit.booking.BookingService
 * @see ru.practicum.shareit.booking.BookingController
 * @since 2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {
    private Integer id;
    private ItemDto item;
    private LocalDateTime start;
    private LocalDateTime end;
    private UserDto booker;
    private Status status;
}
