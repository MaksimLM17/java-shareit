package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.CommentRequestDto;

import java.time.LocalDateTime;
import java.util.List;
/**
 * Репозиторий для работы с сущностями бронирований в базе данных.
 * <p>
 * Предоставляет специализированные методы для выполнения сложных запросов к бронированиям,
 * включая фильтрацию по состояниям, поиск ближайших бронирований и проверку прав доступа.
 * Наследует стандартные CRUD операции от {@link JpaRepository}.
 * </p>
 *
 * <p><b>Особенности реализации:</b></p>
 * <ul>
 *   <li>Использует JPQL запросы для сложных операций фильтрации</li>
 *   <li>Поддерживает все состояния бронирований (ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)</li>
 *   <li>Обеспечивает оптимизированные запросы для работы с временными интервалами</li>
 *   <li>Предоставляет методы для проверки возможности оставления комментариев</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see Booking
 * @see Status
 * @see JpaRepository
 * @see BookingServiceImpl
 * @since 2025
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    /**
     * Находит все бронирования пользователя с фильтрацией по состоянию.
     * <p>
     * Используется для получения списка бронирований, где пользователь является арендатором.
     * Результаты сортируются по дате начала в порядке убывания (последние бронирования first).
     * </p>
     *
     * @param userId идентификатор пользователя-арендатора, не должен быть null
     * @param state состояние для фильтрации, не должен быть null
     * @return список бронирований пользователя, удовлетворяющих условиям фильтрации
     *
     * @apiNote <b>Поддерживаемые состояния:</b>
     * <ul>
     *   <li><b>ALL</b> - все бронирования пользователя</li>
     *   <li><b>CURRENT</b> - текущие бронирования (начались, но еще не закончились)</li>
     *   <li><b>PAST</b> - завершенные бронирования</li>
     *   <li><b>FUTURE</b> - будущие бронирования</li>
     *   <li><b>WAITING</b> - ожидающие подтверждения</li>
     *   <li><b>REJECTED</b> - отклоненные бронирования</li>
     * </ul>
     * @see BookingServiceImpl#getAllBookingsCurrentUser(Integer, String)
     */
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND (:state = 'ALL' OR " +
            "(:state = 'CURRENT' AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP) OR " +
            "(:state = 'PAST' AND b.end < CURRENT_TIMESTAMP) OR " +
            "(:state = 'FUTURE' AND b.start > CURRENT_TIMESTAMP) OR " +
            "(:state = 'WAITING' AND b.status = 'WAITING') OR " +
            "(:state = 'REJECTED' AND b.status = 'REJECTED')) " +
            "ORDER BY b.start DESC")
    List<Booking> getAllBookingsByUserId(@Param("userId") Integer userId, @Param("state") String state);
    /**
     * Находит все бронирования предметов пользователя с фильтрацией по состоянию.
     * <p>
     * Используется для получения списка бронирований, где пользователь является владельцем предметов.
     * Результаты сортируются по дате начала в порядке убывания.
     * </p>
     *
     * @param userId идентификатор пользователя-владельца, не должен быть null
     * @param state состояние для фильтрации, не должен быть null
     * @return список бронирований предметов пользователя, удовлетворяющих условиям фильтрации
     *
     * @apiNote <b>Особенности запроса:</b>
     * <ul>
     *   <li>Использует JOIN с таблицей items для фильтрации по владельцу</li>
     *   <li>Возвращает бронирования всех предметов, принадлежащих пользователю</li>
     *   <li>Поддерживает те же состояния, что и {@link #getAllBookingsByUserId}</li>
     * </ul>
     * @see BookingServiceImpl#getAllBookingsItemsUser(Integer, String)
     */
    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.owner.id = :userId " +
            "AND (:state = 'ALL' OR " +
            "(:state = 'CURRENT' AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP) OR " +
            "(:state = 'PAST' AND b.end < CURRENT_TIMESTAMP) OR " +
            "(:state = 'FUTURE' AND b.start > CURRENT_TIMESTAMP) OR " +
            "(:state = 'WAITING' AND b.status = 'WAITING') OR " +
            "(:state = 'REJECTED' AND b.status = 'REJECTED')) " +
            "ORDER BY b.start DESC")
    List<Booking> getAllBookingsItemsUser(@Param("userId") Integer userId, @Param("state") String state);
    /**
     * Находит последнее завершенное бронирование для указанного предмета.
     * <p>
     * Используется для отображения истории бронирований владельцу предмета.
     * Возвращает самое последнее завершенное бронирование.
     * </p>
     *
     * @param itemId идентификатор предмета, не должен быть null
     * @param status статус бронирования (обычно APPROVED)
     * @param currentTime текущее время для сравнения
     * @return последнее завершенное бронирование или null если не найдено
     *
     * @apiNote <b>Критерии поиска:</b>
     * <ul>
     *   <li>Бронирование для указанного предмета</li>
     *   <li>Указанный статус (обычно APPROVED)</li>
     *   <li>Дата окончания раньше текущего времени</li>
     *   <li>Сортировка по дате окончания (DESC) - самые recent first</li>
     *   <li>LIMIT 1 - только последнее бронирование</li>
     * </ul>
     *
     * @apiNote <b>Использование в сервисе предметов:</b>
     * <pre>
     * {@code
     * Booking lastBooking = bookingRepository.findByItemIdLastBooking(
     *     itemId, Status.APPROVED, LocalDateTime.now()
     * );
     * }
     * </pre>
     *
     * @see ru.practicum.shareit.item.ItemServiceImpl#getById(Integer, Integer)
     */
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = :status " +
            "AND b.end < :currentTime " +
            "ORDER BY b.end DESC " +
            "LIMIT 1")
    Booking findByItemIdLastBooking(@Param("itemId") Integer itemId,
                                    @Param("status") Status status,
                                    @Param("currentTime") LocalDateTime currentTime);
    /**
     * Находит ближайшее будущее бронирование для указанного предмета.
     * <p>
     * Используется для отображения расписания бронирований владельцу предмета.
     * Возвращает самое ближайшее будущее бронирование.
     * </p>
     *
     * @param itemId идентификатор предмета, не должен быть null
     * @param status статус бронирования (обычно APPROVED)
     * @param currentTime текущее время для сравнения
     * @return ближайшее будущее бронирование или null если не найдено
     *
     * @apiNote <b>Критерии поиска:</b>
     * <ul>
     *   <li>Бронирование для указанного предмета</li>
     *   <li>Указанный статус (обычно APPROVED)</li>
     *   <li>Дата начала позже текущего времени</li>
     *   <li>Сортировка по дате начала (ASC) - самые ближайшие first</li>
     *   <li>LIMIT 1 - только ближайшее бронирование</li>
     * </ul>
     *
     * @apiNote <b>Использование в сервисе предметов:</b>
     * <pre>
     * {@code
     * Booking nextBooking = bookingRepository.findByItemIdNextBooking(
     *     itemId, Status.APPROVED, LocalDateTime.now()
     * );
     * }
     * </pre>
     *
     * @see ru.practicum.shareit.item.ItemServiceImpl#getById(Integer, Integer)
     */
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = :status " +
            "AND b.start > :currentTime " +
            "ORDER BY b.start " +
            "LIMIT 1")
    Booking findByItemIdNextBooking(@Param("itemId") Integer itemId,
                                    @Param("status") Status status,
                                    @Param("currentTime") LocalDateTime currentTime);
    /**
     * Проверяет, существует ли завершенное бронирование пользователя для указанного предмета.
     * <p>
     * Используется для валидации возможности оставления комментария.
     * Пользователь может оставить комментарий только если он ранее арендовал предмет.
     * </p>
     *
     * @param userId идентификатор пользователя
     * @param itemId идентификатор предмета
     * @param status статус бронирования (обычно APPROVED)
     * @param currentTime текущее время для проверки завершенности
     * @return true если пользователь арендовал предмет и бронирование завершено
     *
     * @apiNote <b>Бизнес-логика:</b>
     * <ul>
     *   <li>Пользователь должен быть автором бронирования</li>
     *   <li>Бронирование должно быть для указанного предмета</li>
     *   <li>Статус должен быть указанным (обычно APPROVED)</li>
     *   <li>Бронирование должно быть завершено (end <= currentTime)</li>
     * </ul>
     *
     * @apiNote <b>Использование при создании комментария:</b>
     * <pre>
     * {@code
     * boolean canComment = bookingRepository.existsApprovedPastBookingForItem(
     *     userId, itemId, Status.APPROVED, LocalDateTime.now()
     * );
     * if (!canComment) {
     *     throw new BadRequestException("Нельзя оставить комментарий без завершенного бронирования");
     * }
     * }
     * </pre>
     *
     * @see ru.practicum.shareit.item.ItemServiceImpl#createComment(CommentRequestDto, Integer, Integer)
     * */
    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.item.id = :itemId " +
            "AND b.status = :status " +
            "AND b.end <= :currentTime")
    boolean existsApprovedPastBookingForItem(
            @Param("userId") Integer userId,
            @Param("itemId") Integer itemId,
            @Param("status") Status status,
            @Param("currentTime") LocalDateTime currentTime);
}