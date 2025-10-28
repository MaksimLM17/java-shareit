package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.Objects;

/**
 * Класс представляет сущность предмета (вещи) в системе шеринга.
 * <p>
 * Предмет имеет уникальный идентификатор, название, описание, статус доступности,
 * владельца и опциональную связь с запросом на бронирование.
 * </p>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see ru.practicum.shareit.item.ItemRepository
 * @see ru.practicum.shareit.item.ItemService
 * @see User
 * @see ItemRequest
 * @since 2025
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    /**
     * Уникальный идентификатор предмета.
     * <p>
     * Генерируется автоматически базой данных при создании новой записи.
     * Не может быть null.
     * </p>
     *
     * @see GenerationType#IDENTITY
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer id;
    /**
     * Название предмета.
     * <p>
     * Не может быть null или пустым.
     * Должно содержать краткое и понятное описание предмета.
     * </p>
     */
    @Column(name = "name", nullable = false, length = 127)
    private String name;
    /**
     * Подробное описание предмета.
     * <p>
     * Не может быть null или пустым.
     * Содержит детальную информацию о предмете, его состоянии,
     * характеристиках и условиях использования.
     * </p>
     */
    @Column(name = "description", nullable = false, length = 1020)
    private String description;
    /**
     * Статус доступности предмета для бронирования.
     * <p>
     * Определяет, доступен ли предмет в данный момент для аренды.
     * True - предмет доступен для бронирования
     * false - предмет уже забронирован или недоступен
     * </p>
     */
    @Column(name = "available", nullable = false)
    private boolean available;
    /**
     * Владелец предмета.
     * <p>
     * Пользователь, который добавил предмет в систему шеринга.
     * Связь многие-к-одному с сущностью User.
     * Загружается лениво для оптимизации производительности.
     * </p>
     *
     * @see User
     * @see FetchType#LAZY
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;
    /**
     * Запрос на бронирование, связанный с предметом.
     * <p>
     * Опциональная связь с запросом на бронирование.
     * Если предмет был создан в ответ на запрос, содержит ссылку на этот запрос.
     * Загружается лениво для оптимизации производительности.
     * </p>
     *
     * @see ItemRequest
     * @see FetchType#LAZY
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private ItemRequest request;

    /**
     * Конструктор для создания предмета без связи с запросом.
     * <p>
     * Используется при самостоятельном добавлении предмета владельцем,
     * не в ответ на конкретный запрос.
     * </p>
     *
     * @param name название предмета, не должно быть null
     * @param description описание предмета, не должно быть null
     * @param available статус доступности предмета
     * @param owner владелец предмета, не должен быть null
     */
    public Item(String name, String description, boolean available, User owner) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id != null && id.equals(item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
