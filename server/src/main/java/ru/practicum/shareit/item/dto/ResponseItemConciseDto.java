package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO для краткого представления информации о предмете.
 * <p>
 * Используется для передачи минимального набора данных о предмете
 * в сценариях, где не требуется полная информация (идентификатор, статус доступности, владелец).
 * Содержит только основные описательные поля предмета.
 * </p>
 *
 * <p><b>Контекст использования:</b></p>
 * <ul>
 *   <li>При запросе на получение вещей пользователя</li>
 *   <li>Поиск вещи по имени или описанию</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see ru.practicum.shareit.item.ItemService
 * @since 2025
 */
@Data
@AllArgsConstructor
public class ResponseItemConciseDto {
    private String name;
    private String description;
}
