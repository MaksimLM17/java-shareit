package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * DTO (Data Transfer Object) для входящих данных при создании нового запроса вещи.
 * <p>
 * Используется для получения данных от клиента при создании нового запроса через REST API.
 * Содержит минимально необходимые данные для создания запроса предмета.
 * Не содержит идентификаторов и системных полей, которые генерируются автоматически на сервере.
 * </p>
 *
 * <p><b>Контекст использования:</b></p>
 * <ul>
 *   <li>Создание нового запроса предмета (POST запросы)</li>
 *   <li>Валидация входящих данных от клиента</li>
 *   <li>Прием пользовательского ввода в формах создания запроса</li>
 * </ul>
 *
 * <p><b>Отличие от ItemRequestFullDto:</b></p>
 * <ul>
 *   <li>ItemRequestInDto - для ввода данных (клиент → сервер)</li>
 *   <li>ItemRequestFullDto - для вывода данных (сервер → клиент)</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see ItemRequestFullDto
 * @see ru.practicum.shareit.request.ItemRequest
 * @see ru.practicum.shareit.request.ItemRequestService
 * @see ru.practicum.shareit.request.ItemRequestController
 * @since 2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestInDto {

    private String description;
}
