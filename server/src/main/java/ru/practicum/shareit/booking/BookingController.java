package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;
/**
 * REST контроллер для управления бронированиями в системе шеринга.
 * <p>
 * Предоставляет HTTP endpoints для операций с бронированиями: создание, подтверждение/отклонение,
 * получение информации и фильтрацию по различным состояниям.
 * Обрабатывает входящие HTTP запросы и делегирует выполнение бизнес-логики сервисному слою.
 * </p>
 *
 * <p><b>Базовый путь:</b> {@code /bookings}</p>
 * <p><b>Заголовок аутентификации:</b> {@code X-Sharer-User-Id} - идентификатор пользователя</p>
 *
 * <p><b>Поддерживаемые операции:</b></p>
 * <ul>
 *   <li>Создание нового бронирования</li>
 *   <li>Подтверждение/отклонение бронирования владельцем</li>
 *   <li>Получение информации о конкретном бронировании</li>
 *   <li>Получение списка бронирований пользователя как арендатора</li>
 *   <li>Получение списка бронирований предметов пользователя как владельца</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see BookingService
 * @see BookingServiceImpl
 * @since 2025
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    /**
     * Сервис для выполнения бизнес-логики бронирований.
     */
    private final BookingService bookingService;

    /**
     * Заголовок HTTP запроса для передачи идентификатора пользователя.
     * <p>
     * Используется для аутентификации и авторизации пользователя.
     * Должен присутствовать во всех запросах к API бронирований.
     * </p>
     */
    private static final String USER_ID_IN_HEADER = "X-Sharer-User-Id";

    /**
     * Создает новое бронирование.
     * <p>
     * Принимает JSON с данными бронирования и сохраняет его в системе.
     * Пользователь, указанный в заголовке, становится автором бронирования.
     * Статус нового бронирования устанавливается в {@link ru.practicum.shareit.booking.Status#WAITING}.
     * </p>
     *
     * @param bookingRequestDto DTO с данными для создания бронирования, не должен быть null
     * @param userId идентификатор пользователя-арендатора из заголовка, не должен быть null
     * @return BookingDto созданное бронирование с присвоенным идентификатором
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь или предмет не найдены
     * @throws ru.practicum.shareit.exception.BadRequestException если нарушены бизнес-правила
     *
     * @apiNote <b>HTTP запрос:</b> POST /bookings
     * @apiNote <b>Обязательные заголовки:</b> X-Sharer-User-Id
     * @apiNote <b>Пример тела запроса:</b>
     * <pre>
     * {
     *   "itemId": 123,
     *   "start": "2024-01-20T10:00:00",
     *   "end": "2024-01-22T18:00:00"
     * }
     * </pre>
     * @apiNote <b>HTTP статус ответа:</b> 200 OK
     *
     * @see BookingService#add(BookingRequestDto, Integer)
     */
    @PostMapping
    public BookingDto add(@RequestBody BookingRequestDto bookingRequestDto, @RequestHeader(USER_ID_IN_HEADER) Integer userId) {
        return bookingService.add(bookingRequestDto, userId);
    }
    /**
     * Подтверждает или отклоняет бронирование.
     * <p>
     * Изменяет статус бронирования. Выполнить операцию может только владелец предмета.
     * Бронирование должно находиться в статусе {@link ru.practicum.shareit.booking.Status#WAITING}.
     * </p>
     *
     * @param bookingId идентификатор бронирования для обработки, не должен быть null
     * @param userId идентификатор пользователя-владельца из заголовка, не должен быть null
     * @param approved true для подтверждения, false для отклонения
     * @return BookingDto обновленное бронирование с новым статусом
     * @throws ru.practicum.shareit.exception.NotFoundException если бронирование не найдено
     * @throws ru.practicum.shareit.exception.BadRequestException если пользователь не является владельцем
     *         или бронирование уже обработано
     *
     * @apiNote <b>HTTP запрос:</b> PATCH /bookings/{bookingId}?approved={true|false}
     * @apiNote <b>Обязательные заголовки:</b> X-Sharer-User-Id
     * @apiNote <b>Примеры:</b>
     * <pre>
     * // Подтверждение бронирования
     * PATCH /bookings/456?approved=true
     *
     * // Отклонение бронирования
     * PATCH /bookings/456?approved=false
     * </pre>
     * @apiNote <b>HTTP статус ответа:</b> 200 OK
     *
     * @see BookingService#approve(Integer, Integer, boolean)
     */
    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable Integer bookingId, @RequestHeader(USER_ID_IN_HEADER) Integer userId,
                              @RequestParam Boolean approved) {
        return bookingService.approve(bookingId, userId, approved);
    }
    /**
     * Возвращает бронирование по идентификатору.
     * <p>
     * Информацию о бронировании могут получить только владелец предмета или автор бронирования.
     * </p>
     *
     * @param bookingId идентификатор запрашиваемого бронирования, не должен быть null
     * @param userId идентификатор пользователя, запрашивающего информацию, не должен быть null
     * @return BookingDto информация о бронировании
     * @throws ru.practicum.shareit.exception.NotFoundException если бронирование не найдено
     * @throws ru.practicum.shareit.exception.BadRequestException если пользователь не имеет прав доступа
     *
     * @apiNote <b>HTTP запрос:</b> GET /bookings/{bookingId}
     * @apiNote <b>Обязательные заголовки:</b> X-Sharer-User-Id
     * @apiNote <b>Пример ответа:</b>
     * <pre>
     * {
     *   "id": 456,
     *   "start": "2024-01-20T10:00:00",
     *   "end": "2024-01-22T18:00:00",
     *   "status": "APPROVED",
     *   "item": {
     *     "id": 123,
     *     "name": "Аккумуляторная дрель",
     *     "description": "Мощная дрель с двумя аккумуляторами"
     *   },
     *   "booker": {
     *     "id": 789,
     *     "name": "Иван Иванов",
     *     "email": "ivan@example.com"
     *   }
     * }
     * </pre>
     * @apiNote <b>HTTP статус ответа:</b> 200 OK
     *
     * @see BookingService#getById(Integer, Integer)
     */
    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable Integer bookingId,
                              @RequestHeader(USER_ID_IN_HEADER) Integer userId) {
        return bookingService.getById(bookingId, userId);
    }

    /**
     * Возвращает список бронирований текущего пользователя с фильтрацией по состоянию.
     * <p>
     * Предоставляет бронирования, где пользователь является арендатором (booker).
     * Поддерживает фильтрацию по различным состояниям бронирований.
     * </p>
     *
     * @param userId идентификатор пользователя-арендатора из заголовка, не должен быть null
     * @param state состояние для фильтрации, по умолчанию "ALL"
     * @return список BookingDto с бронированиями пользователя
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь не найден
     * @throws ru.practicum.shareit.exception.BadRequestException если указано некорректное состояние
     *
     * @apiNote <b>HTTP запрос:</b> GET /bookings?state={state}
     * @apiNote <b>Обязательные заголовки:</b> X-Sharer-User-Id
     * @apiNote <b>Поддерживаемые состояния:</b>
     * <ul>
     *   <li><b>ALL</b> - все бронирования (по умолчанию)</li>
     *   <li><b>CURRENT</b> - текущие бронирования</li>
     *   <li><b>PAST</b> - завершенные бронирования</li>
     *   <li><b>FUTURE</b> - будущие бронирования</li>
     *   <li><b>WAITING</b> - ожидающие подтверждения</li>
     *   <li><b>REJECTED</b> - отклоненные бронирования</li>
     * </ul>
     * @apiNote <b>Примеры запросов:</b>
     * <pre>
     * GET /bookings                    // все бронирования
     * GET /bookings?state=CURRENT      // текущие бронирования
     * GET /bookings?state=PAST         // завершенные бронирования
     * GET /bookings?state=FUTURE       // будущие бронирования
     * </pre>
     * @apiNote <b>HTTP статус ответа:</b> 200 OK
     *
     * @see BookingService#getAllBookingsCurrentUser(Integer, String)
     */
    @GetMapping
    public List<BookingDto> getAllBookingsCurrentUser(@RequestHeader(USER_ID_IN_HEADER) Integer userId,
                                                      @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsCurrentUser(userId, state.toUpperCase());
    }

    /**
     * Возвращает список бронирований предметов пользователя с фильтрацией по состоянию.
     * <p>
     * Предоставляет бронирования предметов, принадлежащих пользователю (владельцу).
     * Поддерживает фильтрацию по различным состояниям бронирований.
     * </p>
     *
     * @param userId идентификатор пользователя-владельца из заголовка, не должен быть null
     * @param state состояние для фильтрации, по умолчанию "ALL"
     * @return список BookingDto с бронированиями предметов пользователя
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь не найден
     * @throws ru.practicum.shareit.exception.BadRequestException если указано некорректное состояние
     *
     * @apiNote <b>HTTP запрос:</b> GET /bookings/owner?state={state}
     * @apiNote <b>Обязательные заголовки:</b> X-Sharer-User-Id
     * @apiNote <b>Поддерживаемые состояния:</b> те же, что и для {@link #getAllBookingsCurrentUser}
     * @apiNote <b>Примеры запросов:</b>
     * <pre>
     * GET /bookings/owner               // все бронирования предметов
     * GET /bookings/owner?state=WAITING // ожидающие подтверждения бронирования
     * GET /bookings/owner?state=PAST    // завершенные бронирования предметов
     * </pre>
     * @apiNote <b>HTTP статус ответа:</b> 200 OK
     *
     * @see BookingService#getAllBookingsItemsUser(Integer, String)
     */
    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsItemsUser(@RequestHeader(USER_ID_IN_HEADER) Integer userId,
                                                    @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsItemsUser(userId, state.toUpperCase());
    }
}
