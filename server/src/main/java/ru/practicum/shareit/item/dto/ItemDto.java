package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

/**
 * DTO (Data Transfer Object) класс для передачи данных о предмете (вещи).
 * <p>
 * Используется для обмена данными между клиентом и сервером через REST API.
 * Содержит основные поля предмета для отображения и создания.
 * Не содержит бизнес-логики и аннотаций JPA.
 * </p>
 *
 * <p><b>Особенности:</b></p>
 * <ul>
 *   <li>Содержит идентификатор владельца вещи</li>
 *   <li>Содержит идентификатор связанного запроса</li>
 *   <li>Используется для операций создания и обновления предметов</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see ru.practicum.shareit.item.model.Item
 * @see ru.practicum.shareit.item.ItemController
 * @see ru.practicum.shareit.item.ItemService
 * @since 2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer ownerId;
    /**
     * Идентификатор запроса на бронирование.
     * <p>
     * Связывает предмет с конкретным запросом на бронирование.
     * Если предмет был создан в ответ на запрос, содержит идентификатор этого запроса.
     * Может быть null, если предмет добавлен не в ответ на запрос.
     * </p>
     */
    private Integer requestId;
}
