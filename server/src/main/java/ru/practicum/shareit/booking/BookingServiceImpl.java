package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Objects;
/**
 * Реализация сервиса для управления бронированиями в системе шеринга.
 * <p>
 * Предоставляет бизнес-логику для операций с бронированиями: создание, подтверждение,
 * получение информации и фильтрацию по различным состояниям.
 * Обеспечивает проверку прав доступа, валидацию данных и обработку бизнес-правил.
 * </p>
 *
 * <p><b>Особенности реализации:</b></p>
 * <ul>
 *   <li>Использует Spring Data JPA репозитории для работы с данными</li>
 *   <li>Применяет мапперы для преобразования между DTO и entity</li>
 *   <li>Обеспечивает подробное логирование всех операций</li>
 *   <li>Обрабатывает бизнес-исключения (NotFoundException, BadRequestException)</li>
 *   <li>Использует транзакции для гарантии целостности данных</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see BookingService
 * @see BookingRepository
 * @see BookingMapper
 * @since 2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    /**
     * Репозиторий для работы с данными предметов.
     */
    private final ItemRepository itemRepository;

    /**
     * Репозиторий для работы с данными бронирований.
     */
    private final BookingRepository bookingRepository;

    /**
     * Репозиторий для работы с данными пользователей.
     */
    private final UserRepository userRepository;

    /**
     * Маппер для преобразования между сущностями бронирований и DTO.
     */
    private final BookingMapper bookingMapper;

    /**
     * Создает новое бронирование.
     * <p>
     * Выполняет проверку бизнес-правил перед созданием бронирования:
     * - Пользователь не может бронировать собственные предметы
     * - Предмет должен быть доступен для бронирования
     * - Даты бронирования должны быть корректными
     * </p>
     *
     * @param bookingRequestDto DTO с данными для создания бронирования
     * @param userId идентификатор пользователя, создающего бронирование
     * @return BookingDto созданное бронирование с присвоенным идентификатором
     * @throws NotFoundException если пользователь или предмет не найдены
     * @throws BadRequestException если нарушены бизнес-правила
     *
     * @see BookingService#add(BookingRequestDto, Integer)
     */
    @Override
    @Transactional
    public BookingDto add(BookingRequestDto bookingRequestDto, Integer userId) {
        log.debug("Получен запрос на создание брони пользователем с id = {}, " +
                "для вещи с id = {}", userId, bookingRequestDto.getItemId());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден!"));
        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id " + bookingRequestDto.getItemId() + " не найдена!"));

        if (item.getOwner().getId().equals(userId)) {
            log.error("Попытка забронировать свою вещь!");
            throw new BadRequestException("Нельзя бронировать свою же вещь");
        }

        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            log.error("Указаны некорректные даты начала и конца бронирования: дата начала = {}, дата конца = {}",
                    bookingRequestDto.getStart(), bookingRequestDto.getEnd());
            throw new BadRequestException("Дата начала должна быть раньше даты окончания");
        }

        Booking booking = bookingMapper.mapToModelFromRequest(bookingRequestDto);
        // Проверка доступности предмета
        if (item.isAvailable()) {
            booking.setItem(item);
            booking.setBooker(user);
            booking.setStatus(Status.WAITING);
            Booking savedBooking = bookingRepository.save(booking);
            log.info("Вещь с id = {}, забронирована, пользователем с id {}", item.getId(), booking.getBooker().getId());
            return bookingMapper.mapToDto(savedBooking);
        } else {
            log.error("Вещь с id = {}, недоступна для бронирования", item.getId());
            throw new BadRequestException("Вещь недоступна для бронирования");
        }
    }
    /**
     * Подтверждает или отклоняет бронирование.
     * <p>
     * Выполняет изменение статуса бронирования владельцем предмета.
     * Проверяет, что пользователь является владельцем предмета
     * и бронирование находится в статусе WAITING.
     * </p>
     *
     * @param bookingId идентификатор бронирования для обработки
     * @param userId идентификатор пользователя, обрабатывающего бронирование
     * @param approved true для подтверждения (APPROVED), false для отклонения (REJECTED)
     * @return BookingDto обновленное бронирование с новым статусом
     * @throws NotFoundException если бронирование не найдено
     * @throws BadRequestException если пользователь не является владельцем или бронирование уже обработано
     *
     * @see BookingService#approve(Integer, Integer, boolean)
     */
    @Override
    @Transactional
    public BookingDto approve(Integer bookingId, Integer userId, boolean approved) {
        log.info("Получен запрос на обновление статуса брони с данными: bookingId = {}, userId = {}, approved = {}",
                bookingId, userId, approved);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено!"));

        // Проверка прав доступа - только владелец предмета может менять статус
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new BadRequestException("Обновление статуса бронирования доступно только владельцам вещи!");
        }
        // Проверка, что бронирование еще не обработано
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BadRequestException("Нельзя изменить статус уже подтверждённого бронирования");
        }
        // Обработка заявки бронирования
        if (approved) {
            booking.setStatus(Status.APPROVED);
            bookingRepository.save(booking);
            log.info("Бронирование подтверждено! С данными: bookingId = {}, itemId = {}",
                    booking.getId(), booking.getItem().getId());
        } else {
            booking.setStatus(Status.REJECTED);
            bookingRepository.save(booking);
            log.info("Бронирование отклонено! С данными: bookingId = {}, itemId = {}",
                    booking.getId(), booking.getItem().getId());
        }

        return bookingMapper.mapToDto(booking);
    }

    /**
     * Возвращает бронирование по идентификатору.
     * <p>
     * Проверяет права доступа - информацию о бронировании могут получить
     * только владелец предмета или автор бронирования.
     * </p>
     *
     * @param bookingId идентификатор запрашиваемого бронирования
     * @param userId идентификатор пользователя, запрашивающего информацию
     * @return BookingDto информация о бронировании
     * @throws NotFoundException если бронирование или пользователь не найдены
     * @throws BadRequestException если пользователь не имеет прав доступа
     *
     * @see BookingService#getById(Integer, Integer)
     */
    @Override
    @Transactional(readOnly = true)
    public BookingDto getById(Integer bookingId, Integer userId) {
        log.debug("Получен запрос на просмотр бронирования по id = {}", bookingId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден!"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено!"));
        // Проверка прав доступа
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId) &&
                !Objects.equals(booking.getBooker().getId(), userId)) {
            log.error("Попытка посмотреть детали бронирования пользователем с id = {}, " +
                    "который не является автором бронирования и владельцем вещи!", userId);
            throw new BadRequestException("Пользователь с id " + userId + " не является владельцем вещи или автором бронирования!");
        }
        return bookingMapper.mapToDto(booking);
    }

    /**
     * Возвращает список бронирований текущего пользователя с фильтрацией по состоянию.
     * <p>
     * Предоставляет бронирования, где пользователь является арендатором (booker).
     * Поддерживает фильтрацию по различным состояниям бронирований.
     * </p>
     *
     * @param userId идентификатор пользователя-арендатора
     * @param state состояние для фильтрации
     * @return список BookingDto с бронированиями пользователя
     * @throws NotFoundException если пользователь не найден
     * @throws BadRequestException если указано некорректное состояние
     *
     * @see BookingService#getAllBookingsCurrentUser(Integer, String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookingsCurrentUser(Integer userId, String state) {
        log.debug("Получен запрос на получение всех бронирований пользователя {}, со статусом {}", userId, state);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден!"));
        validateState(state);

        List<Booking> bookings = bookingRepository.getAllBookingsByUserId(userId, state);
        log.debug("Отправлен список бронирований пользователя размером {}", bookings.size());
        return bookings.stream()
                .map(bookingMapper::mapToDto)
                .toList();
    }

    /**
     * Возвращает список бронирований предметов пользователя с фильтрацией по состоянию.
     * <p>
     * Предоставляет бронирования предметов, принадлежащих пользователю (владельцу).
     * Поддерживает фильтрацию по различным состояниям бронирований.
     * </p>
     *
     * @param userId идентификатор пользователя-владельца
     * @param state состояние для фильтрации
     * @return список BookingDto с бронированиями предметов пользователя
     * @throws NotFoundException если пользователь не найден
     * @throws BadRequestException если указано некорректное состояние
     *
     * @see BookingService#getAllBookingsItemsUser(Integer, String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookingsItemsUser(Integer userId, String state) {
        log.debug("Получен запрос на получение всех бронирований для вещей пользователя {}, со статусом {}", userId, state);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден!"));
        validateState(state);

        List<Booking> bookings = bookingRepository.getAllBookingsItemsUser(userId, state);
        log.debug("Отправлен список всех бронирований вещей пользователя размером {}", bookings.size());
        return bookings.stream()
                .map(bookingMapper::mapToDto)
                .toList();
    }
    /**
     * Проверяет корректность значения состояния бронирования.
     * <p>
     * Вспомогательный метод для валидации параметра state перед выполнением запроса.
     * Поддерживаемые значения: ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED.
     * </p>
     *
     * @param state значение состояния для проверки
     * @throws BadRequestException если указано неизвестное значение состояния
     */
    private void validateState(String state) {
        if (!List.of("ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED").contains(state)) {
            log.error("Передано неизвестное значение статуса = {}", state);
            throw new BadRequestException("Неизвестное значение статуса: " + state);
        }
    }
}
