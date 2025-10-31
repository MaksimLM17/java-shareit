package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemResponseInRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * DTO (Data Transfer Object) для передачи расширенной информации о запросе вещи с ответами.
 * <p>
 * Используется для отображения полной информации о запросе вместе со списком предметов,
 * которые были предложены в ответ на данный запрос. Содержит как базовые данные запроса,
 * так и коллекцию связанных предметов-ответов.
 * </p>
 *
 * <p><b>Контекст использования:</b></p>
 * <ul>
 *   <li>Получение детальной информации о запросе со списком предложенных предметов</li>
 *   <li>Отображение страницы запроса с ответами пользователей</li>
 *   <li>API endpoints для получения расширенной информации о запросах</li>
 *   <li>Интеграционные сценарии, требующие полных данных о запросе и ответах</li>
 * </ul>
 *
 * <p><b>Отличие от других DTO:</b></p>
 * <ul>
 *   <li>ItemRequestInDto - только для создания запроса (входные данные)</li>
 *   <li>ItemRequestFullDto - базовая информация о запросе без ответов</li>
 *   <li>ItemRequestWithResponsesDto - полная информация о запросе со списком ответов</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see ItemRequestInDto
 * @see ItemRequestFullDto
 * @see ItemResponseInRequestDto
 * @see ru.practicum.shareit.request.ItemRequest
 * @see ru.practicum.shareit.request.ItemRequestService
 * @see ru.practicum.shareit.request.ItemRequestController
 * @since 2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestWithResponsesDto {

    private Integer id;
    private String description;
    private LocalDateTime created;
    private List<ItemResponseInRequestDto> items = new ArrayList<>();

}
