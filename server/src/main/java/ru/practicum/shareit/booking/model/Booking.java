package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Сущность бронирования предмета в системе шеринга.
 * <p>
 * Представляет запрос на аренду предмета от одного пользователя другому.
 * Содержит информацию о периоде бронирования, статусе и участниках сделки.
 * </p>
 *
 * <p><b>Жизненный цикл бронирования:</b></p>
 * <ul>
 *   <li><b>WAITING</b> - ожидает подтверждения владельцем</li>
 *   <li><b>APPROVED</b> - подтверждено владельцем</li>
 *   <li><b>REJECTED</b> - отклонено владельцем</li>
 *   <li><b>CANCELLED</b> - отменено пользователем</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see Status
 * @see Item
 * @see User
 * @see ru.practicum.shareit.booking.BookingRepository
 * @see ru.practicum.shareit.booking.BookingService
 * @since 2025
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    /**
     * Уникальный идентификатор бронирования.
     * <p>
     * Генерируется автоматически базой данных при создании новой записи.
     * Не может быть null.
     * </p>
     *
     * @see GenerationType#IDENTITY
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Дата и время начала бронирования.
     * <p>
     * Определяет, когда пользователь планирует начать использование предмета.
     * Не может быть null. Должна быть в будущем относительно момента создания бронирования.
     * </p>
     */
    @Column(name = "start_date")
    private LocalDateTime start;
    /**
     * Дата и время окончания бронирования.
     * <p>
     * Определяет, когда пользователь планирует вернуть предмет.
     * Не может быть null. Должна быть после даты начала.
     * </p>
     */
    @Column(name = "end_date")
    private LocalDateTime end;
    /**
     * Предмет, который бронируется.
     * <p>
     * Связь многие-к-одному с сущностью Item.
     * Загружается лениво для оптимизации производительности.
     * Не может быть null.
     * </p>
     *
     * @see Item
     * @see FetchType#LAZY
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;
    /**
     * Пользователь, который бронирует предмет.
     * <p>
     * Связь многие-к-одному с сущностью User.
     * Загружается лениво для оптимизации производительности.
     * Не может быть null. Не может быть владельцем предмета.
     * </p>
     *
     * @see User
     * @see FetchType#LAZY
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id")
    private User booker;
    /**
     * Статус бронирования.
     * <p>
     * Определяет текущее состояние бронирования в системе.
     * Хранится как строка в базе данных.
     * Не может быть null.
     * </p>
     *
     * @see Status
     * @see EnumType#STRING
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return id != null && id.equals(booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

