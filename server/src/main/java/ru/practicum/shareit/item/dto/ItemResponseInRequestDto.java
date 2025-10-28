package ru.practicum.shareit.item.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * DTO для отображения информации о предмете в ответах на запросы бронирования.
 * <p>
 * Используется для представления данных о предмете в контексте запроса на бронирование.
 * Содержит минимальный набор полей, необходимых для идентификации предмета
 * и его связи с пользователем-владельцем.
 * </p>
 *
 * <p><b>Контекст использования:</b></p>
 * <ul>
 *   <li>Ответы на API запросы связанные с бронированиями</li>
 *   <li>Отображение предметов в списках запросов</li>
 *   <li>Упрощенное представление предмета без детальной информации</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see ru.practicum.shareit.request.ItemRequestService
 * @see ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto
 * @since 2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseInRequestDto {

    private Integer id;
    private String name;
    private Integer userId;
}
