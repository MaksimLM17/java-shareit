package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentRequestDto;
import ru.practicum.shareit.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.ResponseItemConciseDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.util.AppHeaders;

import java.util.Collection;

/**
 * REST контроллер для управления предметами (вещами) в системе шеринга.
 * <p>
 * Предоставляет HTTP endpoints для операций с предметами: создание, обновление,
 * поиск, получение информации и управление комментариями.
 * Обрабатывает входящие HTTP запросы и делегирует выполнение бизнес-логики сервисному слою.
 * </p>
 *
 * <p><b>Базовый путь:</b> {@code /items}</p>
 * <p><b>Заголовок аутентификации:</b> {@code X-Sharer-User-Id} - идентификатор пользователя</p>
 *
 * <p><b>Поддерживаемые операции:</b></p>
 * <ul>
 *   <li>Создание нового предмета</li>
 *   <li>Обновление данных предмета</li>
 *   <li>Получение информации о предмете</li>
 *   <li>Получение списка предметов пользователя</li>
 *   <li>Поиск предметов по названию и описанию</li>
 *   <li>Создание комментариев к предметам</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see ItemService
 * @see ItemServiceImpl
 * @since 2025
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    /**
     * Сервис для выполнения бизнес-логики предметов.
     */
    private final ItemService itemService;

    /**
     * Создает новый предмет в системе.
     * <p>
     * Принимает JSON с данными предмета и сохраняет его в системе.
     * Пользователь, указанный в заголовке, становится владельцем предмета.
     * </p>
     *
     * @param userId идентификатор пользователя-владельца из заголовка
     * @param itemDto DTO объект с данными для создания предмета
     * @return ItemDto созданный предмет с присвоенным идентификатором
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь не найден
     * @throws ru.practicum.shareit.exception.BadRequestException при нарушении бизнес-правил
     *
     * @apiNote <b>HTTP запрос:</b> POST /items
     * @apiNote <b>Обязательные заголовки:</b> X-Sharer-User-Id
     * @apiNote <b>Пример тела запроса:</b>
     * <pre>
     * {
     *   "name": "Аккумуляторная дрель",
     *   "description": "Мощная дрель с двумя аккумуляторами",
     *   "available": true,
     *   "requestId": 123
     * }
     * </pre>
     *
     * @see ItemService#create(Integer, ItemDto)
     */    @PostMapping
    public ItemDto create(@RequestHeader(AppHeaders.USER_ID) Integer userId, @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }
    /**
     * Обновляет данные существующего предмета.
     * <p>
     * Выполняет частичное обновление данных предмета. Обновляются только те поля,
     * которые переданы в запросе и не равны null.
     * Обновлять предмет может только его владелец.
     * </p>
     *
     * @param userId идентификатор пользователя из заголовка
     * @param itemId идентификатор предмета для обновления
     * @param updateItemDto DTO объект с данными для обновления
     * @return ItemDto обновленный предмет
     * @throws ru.practicum.shareit.exception.NotFoundException если предмет или пользователь не найдены
     * @throws ru.practicum.shareit.exception.BadRequestException если пользователь не является владельцем
     *
     * @apiNote <b>HTTP запрос:</b> PATCH /items/{itemId}
     * @apiNote <b>Обязательные заголовки:</b> X-Sharer-User-Id
     * @apiNote <b>Пример тела запроса:</b>
     * <pre>
     * {
     *   "name": "Новое название дрели",
     *   "description": "Обновленное описание",
     *   "available": false
     * }
     * </pre>
     *
     * @see ItemService#update(Integer, Integer, UpdateItemDto)
     */
    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(AppHeaders.USER_ID) Integer userId,
                          @PathVariable Integer itemId, @RequestBody UpdateItemDto updateItemDto) {
        return itemService.update(userId, itemId, updateItemDto);
    }
    /**
     * Возвращает предмет по идентификатору с расширенной информацией.
     * <p>
     * Для владельца предмета включает информацию о ближайших бронированиях.
     * Для всех пользователей включает список комментариев.
     * </p>
     *
     * @param userId идентификатор пользователя, запрашивающего информацию
     * @param itemId идентификатор запрашиваемого предмета
     * @return ItemWithBookingDto предмет с информацией о бронированиях и комментариями
     * @throws ru.practicum.shareit.exception.NotFoundException если предмет не найден
     *
     * @apiNote <b>HTTP запрос:</b> GET /items/{itemId}
     * @apiNote <b>Обязательные заголовки:</b> X-Sharer-User-Id
     * @apiNote <b>Пример ответа для владельца:</b>
     * <pre>
     * {
     *   "id": 1,
     *   "name": "Дрель",
     *   "description": "Аккумуляторная дрель",
     *   "available": true,
     *   "lastBooking": { ... },
     *   "nextBooking": { ... },
     *   "comments": [ ... ]
     * }
     * </pre>
     *
     * @see ItemService#getById(Integer, Integer)
     */
    @GetMapping("/{itemId}")
    public ItemWithBookingDto getById(@RequestHeader(AppHeaders.USER_ID) Integer userId, @PathVariable Integer itemId) {
        return itemService.getById(userId,itemId);
    }
    /**
     * Возвращает список предметов пользователя с пагинацией.
     * <p>
     * Предоставляет компактное представление предметов без детальной информации
     * о бронированиях. Используется для отображения в личном кабинете владельца.
     * </p>
     *
     * @param userId идентификатор пользователя-владельца
     * @param from начальный элемент для пагинации (offset), по умолчанию 0
     * @param size количество элементов на странице (limit), по умолчанию 10
     * @return коллекция ResponseItemConciseDto с компактной информацией о предметах
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь не найден
     * @throws ru.practicum.shareit.exception.BadRequestException при некорректных параметрах пагинации
     *
     * @apiNote <b>HTTP запрос:</b> GET /items?from=0&size=10
     * @apiNote <b>Обязательные заголовки:</b> X-Sharer-User-Id
     * @apiNote <b>Пример ответа:</b>
     * <pre>
     * [
     *   {
     *     "name": "Дрель",
     *     "description": "Аккумуляторная дрель"
     *   },
     *   {
     *     "name": "Перфоратор",
     *     "description": "Мощный перфоратор для бетона"
     *   }
     * ]
     * </pre>
     *
     * @see ItemService#getItemsForUser(Integer, Integer, Integer)
     */
    @GetMapping
    public Collection<ResponseItemConciseDto> getItemsForUser(@RequestHeader(AppHeaders.USER_ID) Integer userId,
                                                              @RequestParam(defaultValue = "0") Integer from,
                                                              @RequestParam(defaultValue = "10") Integer size) {
        return itemService.getItemsForUser(userId, from,size);
    }
    /**
     * Выполняет поиск предметов по названию и описанию.
     * <p>
     * Поиск осуществляется только среди доступных предметов (available = true).
     * Возвращает компактное представление результатов для оптимизации.
     * При пустом поисковом запросе возвращает пустую коллекцию.
     * </p>
     *
     * @param text текст для поиска
     * @return коллекция ResponseItemConciseDto с результатами поиска
     *
     * @apiNote <b>HTTP запрос:</b> GET /items/search?text=дрель
     * @apiNote <b>Пример ответа:</b>
     * <pre>
     * [
     *   {
     *     "name": "Аккумуляторная дрель",
     *     "description": "Мощная дрель с двумя аккумуляторами"
     *   },
     *   {
     *     "name": "Дрель-шуруповерт",
     *     "description": "Компактная дрель для дома"
     *   }
     * ]
     * </pre>
     *
     * @see ItemService#searchItems(String)
     */
    @GetMapping("/search")
    public Collection<ResponseItemConciseDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    /**
     * Создает комментарий к предмету.
     * <p>
     * Проверяет, что пользователь действительно брал предмет в аренду
     * и завершил бронирование. Комментарий может оставить только пользователь,
     * который ранее арендовал данный предмет.
     * </p>
     *
     * @param commentRequestDto DTO с данными комментария
     * @param itemId идентификатор предмета, к которому оставляется комментарий
     * @param userId идентификатор пользователя, оставляющего комментарий
     * @return CommentResponseDto созданный комментарий с информацией об авторе
     * @throws ru.practicum.shareit.exception.NotFoundException если предмет или пользователь не найдены
     * @throws ru.practicum.shareit.exception.BadRequestException если пользователь не арендовал предмет
     *
     * @apiNote <b>HTTP запрос:</b> POST /items/{itemId}/comment
     * @apiNote <b>Обязательные заголовки:</b> X-Sharer-User-Id
     * @apiNote <b>Пример тела запроса:</b>
     * <pre>
     * {
     *   "text": "Отличная дрель, всем рекомендую!"
     * }
     * </pre>
     * @apiNote <b>Пример ответа:</b>
     * <pre>
     * {
     *   "id": 1,
     *   "text": "Отличная дрель, всем рекомендую!",
     *   "itemId": 123,
     *   "authorName": "Иван Иванов",
     *   "created": "2024-01-15T10:30:00"
     * }
     * </pre>
     *
     * @see ItemService#createComment(CommentRequestDto, Integer, Integer)
     */
    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(@RequestBody CommentRequestDto commentRequestDto, @PathVariable Integer itemId,
                                            @RequestHeader(AppHeaders.USER_ID) Integer userId) {
        return itemService.createComment(commentRequestDto, itemId, userId);
    }
}
