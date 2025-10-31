package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;

import java.util.List;
/**
 * Сервисный интерфейс для управления запросами на предметы.
 * <p>
 * Определяет контракт для операций создания, поиска и получения запросов на предметы.
 * Реализации этого интерфейса обеспечивают бизнес-логику работы с запросами.
 * </p>
 *
 * <p><b>Основные возможности:</b></p>
 * <ul>
 *   <li>Создание новых запросов на предметы</li>
 *   <li>Получение собственных запросов пользователя</li>
 *   <li>Получение запросов других пользователей</li>
 *   <li>Поиск запроса по идентификатору</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see ItemRequestServiceImpl
 * @see ItemRequestController
 * @see ItemRequestFullDto
 * @see ItemRequestInDto
 * @see ItemRequestWithResponsesDto
 * @since 2025
 */
public interface ItemRequestService {
    /**
     * Создает новый запрос на предмет от имени указанного пользователя.
     * <p>
     * Принимает данные для создания запроса, валидирует их и сохраняет в системе.
     * Временная метка создания устанавливается автоматически.
     * </p>
     *
     * @param userId идентификатор пользователя, создающего запрос
     * @param itemRequestInDto DTO с данными для создания запроса
     * @return ItemRequestFullDto созданный запрос с присвоенным идентификатором
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь не найден
     *
     * @apiNote POST /requests
     */
    ItemRequestFullDto create(Integer userId, ItemRequestInDto itemRequestInDto);

    /**
     * Возвращает все запросы текущего пользователя с ответами.
     * <p>
     * Возвращает список запросов, созданных указанным пользователем, вместе с предметами,
     * которые были предложены в ответ на каждый запрос. Запросы сортируются по дате создания
     * в порядке убывания (от новых к старым).
     * </p>
     *
     * @param userId идентификатор пользователя, чьи запросы требуется получить
     * @return List<ItemRequestWithResponsesDto> список запросов пользователя с ответами
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь не найден
     *
     * @apiNote GET /requests
     */
    List<ItemRequestWithResponsesDto> getAllOwn(Integer userId);

    /**
     * Возвращает все запросы других пользователей.
     * <p>
     * Возвращает список запросов, созданных всеми пользователями, кроме указанного.
     * Используется для просмотра доступных запросов, на которые можно предложить свои предметы.
     * Запросы сортируются по дате создания в порядке убывания.
     * </p>
     *
     * @param userId идентификатор пользователя, исключаемого из результатов
     * @return List<ItemRequestFullDto> список запросов других пользователей
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь не найден
     *
     * @apiNote GET /requests/all
     */
    List<ItemRequestFullDto> getAllOthers(Integer userId);

    /**
     * Возвращает запрос по идентификатору с полной информацией об ответах.
     * <p>
     * Находит запрос по идентификатору и возвращает его вместе со списком всех предметов,
     * которые были предложены в ответ на этот запрос. Доступно для всех аутентифицированных пользователей.
     * </p>
     *
     * @param requestId идентификатор запроса для поиска
     * @return ItemRequestWithResponsesDto запрос с полной информацией об ответах
     * @throws ru.practicum.shareit.exception.NotFoundException если запрос не найден
     *
     * @apiNote GET /requests/{requestId}
     */
    ItemRequestWithResponsesDto getById(Integer requestId);
}
