package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.CommentDto;

import java.util.ArrayList;
import java.util.List;
/**
 * DTO для расширенного представления информации о предмете с данными о бронированиях.
 * <p>
 * Используется для отображения полной информации о предмете, включая историю бронирований
 * и комментарии. Предназначен для детального просмотра предмета, где пользователю
 * необходима вся доступная информация.
 * </p>
 *
 * <p><b>Особенности данных:</b></p>
 * <ul>
 *   <li>Содержит информацию о ближайших бронированиях (только для владельца)</li>
 *   <li>Включает все комментарии к предмету</li>
 *   <li>Используется в ответах API для детального просмотра предмета</li>
 *   <li>Данные о бронированиях заполняются только для владельца предмета</li>
 * </ul>
 *
 * <p><b>Контекст использования:</b></p>
 * <ul>
 *   <li>Детальная страница предмета</li>
 *   <li>Просмотр истории бронирований владельцем</li>
 *   <li>Отображение всех отзывов и комментариев</li>
 *   <li>API endpoint: GET /items/{itemId}</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see ItemDto
 * @see BookingDto
 * @see CommentDto
 * @see ru.practicum.shareit.item.ItemService#getById(Integer, Integer)
 * @see ru.practicum.shareit.item.ItemController#getById(Integer, Integer)
 * @since 2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemWithBookingDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments = new ArrayList<>();
}
