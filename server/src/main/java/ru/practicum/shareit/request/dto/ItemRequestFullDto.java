package ru.practicum.shareit.request.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) для передачи полной информации о запросе вещи.
 * <p>
 * Используется для обмена данными между клиентом и сервером через REST API.
 * Содержит полную информацию о запросе предмета, включая идентификатор, описание и временные метки.
 * Не содержит бизнес-логики и аннотаций JPA.
 * </p>
 *
 * <p><b>Контекст использования:</b></p>
 * <ul>
 *   <li>Получение информации о запросе предмета (GET запросы)</li>
 *   <li>Отображение детальной информации о запросе</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see ru.practicum.shareit.request.ItemRequest
 * @see ru.practicum.shareit.request.ItemRequestService
 * @see ru.practicum.shareit.request.ItemRequestController
 * @since 2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestFullDto {

    private Integer id;
    private String description;
    private LocalDateTime created;

}
