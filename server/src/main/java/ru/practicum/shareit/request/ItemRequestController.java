package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.util.AppHeaders;

import java.util.List;
/**
 * REST контроллер для управления запросами на предметы.
 * <p>
 * Обеспечивает REST API для операций с запросами: создание, получение собственных и чужих запросов,
 * поиск по идентификатору. Является точкой входа для клиентских приложений.
 * </p>
 *
 * <p><b>Основные endpoints:</b></p>
 * <ul>
 *   <li>POST /requests - создание нового запроса</li>
 *   <li>GET /requests - получение собственных запросов с ответами</li>
 *   <li>GET /requests/all - получение запросов других пользователей</li>
 *   <li>GET /requests/{requestId} - получение запроса по ID с ответами</li>
 * </ul>
 *
 * <p><b>Аутентификация:</b></p>
 * <ul>
 *   <li>Все endpoints (кроме GET /requests/{requestId}) требуют заголовок X-Sharer-User-Id</li>
 *   <li>Идентификатор пользователя передается в заголовке запроса</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see ItemRequestService
 * @see ItemRequestFullDto
 * @see ItemRequestInDto
 * @see ItemRequestWithResponsesDto
 * @since 2025
 */
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    /**
     * Сервис для обработки бизнес-логики запросов на предметы.
     * Внедряется через конструктор благодаря аннотации @RequiredArgsConstructor.
     */
    private final ItemRequestService requestService;
    /**
     * Создает новый запрос на предмет от имени аутентифицированного пользователя.
     * <p>
     * Принимает данные запроса в теле запроса и идентификатор пользователя в заголовке.
     * Валидирует входные данные и возвращает созданный запрос с присвоенным идентификатором.
     * </p>
     *
     * @param userId идентификатор пользователя из заголовка X-Sharer-User-Id
     * @param itemRequestInDto DTO с данными для создания запроса
     * @return ItemRequestFullDto созданный запрос с системными полями
     *
     * @http-method POST
     * @endpoint /requests
     * @request-header X-Sharer-User-Id {Integer} идентификатор пользователя
     * @request-body ItemRequestInDto данные для создания запроса
     * @response-body ItemRequestFullDto созданный запрос
     *
     * @example-request
     * POST /requests
     * Headers: { "X-Sharer-User-Id": 1 }
     * Body: { "description": "Нужен паяльник для ремонта электроники" }
     *
     * @example-response
     * Status: 201 Created
     * Body: {
     *   "id": 1,
     *   "description": "Нужен паяльник для ремонта электроники",
     *   "created": "2023-10-15T14:30:00"
     * }
     */
    @PostMapping
    public ItemRequestFullDto create(@RequestHeader(AppHeaders.USER_ID)Integer userId,
                                     @RequestBody ItemRequestInDto itemRequestInDto) {
        return requestService.create(userId, itemRequestInDto);
    }
    /**
     * Возвращает все запросы текущего пользователя вместе с ответами в виде предметов.
     * <p>
     * Для каждого запроса пользователя возвращает полную информацию включая предметы,
     * которые были предложены другими пользователями в качестве ответов.
     * </p>
     *
     * @param userId идентификатор пользователя из заголовка X-Sharer-User-Id
     * @return List<ItemRequestWithResponsesDto> список запросов пользователя с ответами
     *
     * @http-method GET
     * @endpoint /requests
     * @request-header X-Sharer-User-Id {Integer} идентификатор пользователя
     * @response-body List<ItemRequestWithResponsesDto> список запросов с ответами
     *
     * @example-request
     * GET /requests
     * Headers: { "X-Sharer-User-Id": 1 }
     *
     * @example-response
     * Status: 200 OK
     * Body: [{
     *   "id": 1,
     *   "description": "Нужен паяльник для ремонта электроники",
     *   "created": "2023-10-15T14:30:00",
     *   "items": [
     *     {
     *       "id": 101,
     *       "name": "Паяльник 60W",
     *       "description": "Мощный паяльник с регулировкой температуры",
     *       "available": true,
     *       "requestId": 1
     *     }
     *   ]
     * }]
     */
    @GetMapping
    public List<ItemRequestWithResponsesDto> getAllOwn(@RequestHeader(AppHeaders.USER_ID)Integer userId) {
        return requestService.getAllOwn(userId);
    }
    /**
     * Возвращает все запросы, созданные другими пользователями.
     * <p>
     * Используется для просмотра запросов, на которые текущий пользователь может предложить
     * свои предметы. Исключает из результатов запросы самого пользователя.
     * </p>
     *
     * @param userId идентификатор пользователя из заголовка X-Sharer-User-Id
     * @return List<ItemRequestFullDto> список запросов других пользователей
     *
     * @http-method GET
     * @endpoint /requests/all
     * @request-header X-Sharer-User-Id {Integer} идентификатор пользователя
     * @response-body List<ItemRequestFullDto> список запросов других пользователей
     *
     * @example-request
     * GET /requests/all
     * Headers: { "X-Sharer-User-Id": 1 }
     *
     * @example-response
     * Status: 200 OK
     * Body: [{
     *   "id": 2,
     *   "description": "Ищу книги по программированию",
     *   "created": "2023-10-14T10:15:00"
     * }]
     */
    @GetMapping("/all")
    public List<ItemRequestFullDto> getAllOthers(@RequestHeader(AppHeaders.USER_ID)Integer userId) {
        return requestService.getAllOthers(userId);
    }
    /**
     * Возвращает запрос по идентификатору с полной информацией об ответах.
     * <p>
     * Находит запрос по ID и возвращает его вместе со списком всех предметов,
     * которые были предложены в ответ на этот запрос. Доступно для всех аутентифицированных пользователей.
     * </p>
     *
     * @param requestId идентификатор запроса из пути URL
     * @return ItemRequestWithResponsesDto запрос с полной информацией об ответах
     *
     * @http-method GET
     * @endpoint /requests/{requestId}
     * @path-variable requestId {Integer} идентификатор запроса
     * @response-body ItemRequestWithResponsesDto запрос с ответами
     *
     * @example-request
     * GET /requests/1
     *
     * @example-response
     * Status: 200 OK
     * Body: {
     *   "id": 1,
     *   "description": "Нужен паяльник для ремонта электроники",
     *   "created": "2023-10-15T14:30:00",
     *   "items": [
     *     {
     *       "id": 101,
     *       "name": "Паяльник 60W",
     *       "description": "Мощный паяльник с регулировкой температуры",
     *       "available": true,
     *       "requestId": 1
     *     }
     *   ]
     * }
     */
    @GetMapping("/{requestId}")
    public ItemRequestWithResponsesDto getById(@PathVariable Integer requestId) {
        return requestService.getById(requestId);
    }
}
