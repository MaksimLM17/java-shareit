package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Сущность запроса на предмет в системе шеринга.
 * <p>
 * Представляет запрос пользователя на добавление нового предмета в систему.
 * Другие пользователи могут просматривать запросы и предлагать свои предметы
 * в качестве ответа на запрос.
 * </p>
 *
 * <p><b>Жизненный цикл запроса:</b></p>
 * <ul>
 *   <li>Пользователь создает запрос с описанием нужного предмета</li>
 *   <li>Система автоматически устанавливает дату создания</li>
 *   <li>Другие пользователи видят запрос и могут предложить свои предметы</li>
 *   <li>Предметы, созданные в ответ на запрос, связываются с ним</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see User
 * @see ru.practicum.shareit.item.model.Item
 * @see ru.practicum.shareit.request.ItemRequestRepository
 * @see ru.practicum.shareit.request.ItemRequestService
 * @since 2025
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "requests")
public class ItemRequest {
    /**
     * Уникальный идентификатор запроса.
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
     * Описание запрашиваемого предмета.
     * <p>
     * Содержит детальную информацию о том, какой предмет нужен пользователю.
     * Включает характеристики, особенности, требования к предмету.
     * Не может быть null или пустым.
     * </p>
     */
    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    /**
     * Пользователь, создавший запрос.
     * <p>
     * Связь многие-к-одному с сущностью User.
     * Загружается лениво для оптимизации производительности.
     * Не может быть null.
     * </p>
     *
     * @see User
     * @see FetchType#LAZY
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    @ToString.Exclude
    private User requester;

    /**
     * Дата и время создания запроса.
     * <p>
     * Устанавливается автоматически при создании записи.
     * Не может быть null.
     * Используется для сортировки запросов (новые первыми).
     * </p>
     */
    @Column(name = "created_date", nullable = false)
    private LocalDateTime created;

    /**
     * Конструктор для создания нового запроса.
     * <p>
     * Используется при создании запроса пользователем.
     * Поле created устанавливается автоматически через {@link PrePersist}.
     * </p>
     *
     * @param description описание запрашиваемого предмета
     */
    public ItemRequest(String description) {
        this.description = description;
    }

    /**
     * Callback метод, выполняемый перед сохранением сущности.
     * <p>
     * Автоматически устанавливает текущую дату и время создания запроса.
     * Гарантирует, что поле created всегда заполнено.
     * </p>
     */
    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemRequest itemRequest = (ItemRequest) o;
        return id != null && id.equals(itemRequest.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
