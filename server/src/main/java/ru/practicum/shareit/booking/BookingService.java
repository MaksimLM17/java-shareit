package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;
/**
 * Сервис для управления бронированиями в системе шеринга.
 * <p>
 * Предоставляет бизнес-логику для операций с бронированиями: создание, подтверждение,
 * получение информации.
 * Обеспечивает проверку прав доступа, валидацию данных и обработку бизнес-правил.
 * </p>
 *
 * <p><b>Основные функции:</b></p>
 * <ul>
 *   <li>Создание новых бронирований</li>
 *   <li>Подтверждение/отклонение бронирований владельцами</li>
 *   <li>Получение информации о конкретном бронировании</li>
 *   <li>Получение списка бронирований конкретного пользователя</li>
 *   <li>Получение списка забронированных вещей пользователя</li>
 *   <li>Управление правами доступа к бронированиям</li>
 * </ul>
 *
 * <p><b>Состояния бронирований (state):</b></p>
 * <ul>
 *   <li><b>ALL</b> - все бронирования</li>
 *   <li><b>CURRENT</b> - текущие бронирования</li>
 *   <li><b>PAST</b> - завершенные бронирования</li>
 *   <li><b>FUTURE</b> - будущие бронирования</li>
 *   <li><b>WAITING</b> - ожидающие подтверждения</li>
 *   <li><b>REJECTED</b> - отклоненные бронирования</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see BookingServiceImpl
 * @see BookingController
 * @see BookingRepository
 * @since 2025
 */
public interface BookingService {
    /**
     * Создает новое бронирование.
     * <p>
     * Выполняет проверку бизнес-правил перед созданием бронирования:
     * - Пользователь не может бронировать собственные предметы
     * - Предмет должен быть доступен для бронирования
     * - Даты бронирования должны быть корректными (начало раньше окончания, в будущем)
     * - Пользователь должен существовать в системе
     * </p>
     *
     * @param bookingRequestDto DTO с данными для создания бронирования, не должен быть null
     * @param userId идентификатор пользователя, создающего бронирование, не должен быть null
     * @return BookingDto созданное бронирование с присвоенным идентификатором и статусом WAITING
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь или предмет не найдены
     * @throws ru.practicum.shareit.exception.BadRequestException если нарушены бизнес-правила
     *
     * @see BookingRequestDto
     * @see BookingDto
     */
    BookingDto add(BookingRequestDto bookingRequestDto, Integer userId);
    /**
     * Подтверждает или отклоняет бронирование.
     * <p>
     * Выполняет изменение статуса бронирования владельцем предмета.
     * Проверяет, что пользователь является владельцем предмета
     * и бронирование находится в статусе WAITING.
     * </p>
     *
     * @param bookingId идентификатор бронирования для обработки, не должен быть null
     * @param userId идентификатор пользователя, обрабатывающего бронирование, не должен быть null
     * @param approved true для подтверждения (APPROVED), false для отклонения (REJECTED)
     * @return BookingDto обновленное бронирование с новым статусом
     * @throws ru.practicum.shareit.exception.NotFoundException если бронирование или пользователь не найдены
     * @throws ru.practicum.shareit.exception.BadRequestException если пользователь не является владельцем
     *         или бронирование уже обработано
     *
     * @see BookingDto
     */
    BookingDto approve(Integer bookingId, Integer userId, boolean approved);
    /**
     * Возвращает бронирование по идентификатору.
     * <p>
     * Проверяет права доступа - информацию о бронировании могут получить
     * только владелец предмета или автор бронирования.
     * </p>
     *
     * @param bookingId идентификатор запрашиваемого бронирования, не должен быть null
     * @param userId идентификатор пользователя, запрашивающего информацию, не должен быть null
     * @return BookingDto информация о бронировании
     * @throws ru.practicum.shareit.exception.NotFoundException если бронирование не найдено
     * @throws ru.practicum.shareit.exception.BadRequestException если пользователь не имеет прав доступа
     *
     * @see BookingDto
     */
    BookingDto getById(Integer bookingId, Integer userId);
    /**
     * Возвращает список бронирований текущего пользователя с фильтрацией по состоянию.
     * <p>
     * Предоставляет бронирования, где пользователь является арендатором (booker).
     * Поддерживает фильтрацию по различным состояниям бронирований.
     * </p>
     *
     * @param userId идентификатор пользователя-арендатора, не должен быть null
     * @param state состояние для фильтрации (ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)
     * @return список BookingDto с бронированиями пользователя, отсортированный по дате начала (убывание)
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь не найден
     * @throws ru.practicum.shareit.exception.BadRequestException если указано некорректное состояние
     *
     * @see BookingDto
     */
    List<BookingDto> getAllBookingsCurrentUser(Integer userId, String state);
    /**
     * Возвращает список бронирований предметов пользователя с фильтрацией по состоянию.
     * <p>
     * Предоставляет бронирования предметов, принадлежащих пользователю (владельцу).
     * Поддерживает фильтрацию по различным состояниям бронирований.
     * </p>
     *
     * @param userId идентификатор пользователя-владельца, не должен быть null
     * @param state состояние для фильтрации (ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)
     * @return список BookingDto с бронированиями предметов пользователя, отсортированный по дате начала (убывание)
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь не найден
     * @throws ru.practicum.shareit.exception.BadRequestException если указано некорректное состояние
     *
     * @see BookingDto
     */
    List<BookingDto> getAllBookingsItemsUser(Integer userId, String state);
}
