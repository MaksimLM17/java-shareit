package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
/**
 * Репозиторий для работы с сущностями предметов (вещей) в базе данных.
 * <p>
 * Предоставляет методы для выполнения операций с предметами, включая поиск,
 * фильтрацию и пагинацию. Наследует стандартные CRUD операции от {@link JpaRepository}.
 * </p>
 *
 * <p><b>Особенности реализации:</b></p>
 * <ul>
 *   <li>Использует JPQL запросы для сложных операций поиска</li>
 *   <li>Поддерживает пагинацию через Spring Data Pageable</li>
 *   <li>Обеспечивает поиск с учетом регистра и доступности предметов</li>
 *   <li>Предоставляет методы для работы с запросами на предметы</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see Item
 * @see JpaRepository
 * @see ItemServiceImpl
 * @since 2025
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    /**
     * Находит все предметы пользователя с поддержкой пагинации.
     * <p>
     * Используется для отображения списка предметов владельца в личном кабинете.
     * Запрос выполняется с пагинацией для оптимизации работы с большими объемами данных.
     * </p>
     *
     * @param userId идентификатор пользователя-владельца, не должен быть null
     * @param pageable объект пагинации, содержащий информацию о странице и размере
     * @return страница с предметами пользователя
     *
     * @apiNote <b>Использование в сервисе:</b>
     * <pre>
     * {@code
     * Pageable pageable = PageRequest.of(from / size, size);
     * Page<Item> itemPage = itemRepository.findItemsByUserId(userId, pageable);
     * }
     * </pre>
     *
     * @see ItemServiceImpl#getItemsForUser(Integer, Integer, Integer)
     */
    @Query("SELECT i FROM Item i WHERE i.owner.id = :userId")
    Page<Item> findItemsByUserId(Integer userId, Pageable pageable);
    /**
     * Выполняет поиск доступных предметов по названию и описанию.
     * <p>
     * Поиск осуществляется без учета регистра и возвращает только доступные предметы.
     * Используется для функциональности поиска в приложении.
     * </p>
     *
     * @param text текст для поиска, не должен быть null
     * @return список предметов, удовлетворяющих условиям поиска
     *
     * @apiNote <b>Условия поиска:</b>
     * <ul>
     *   <li>Текст ищется в названии ИЛИ описании</li>
     *   <li>Поиск без учета регистра (LOWER)</li>
     *   <li>Только доступные предметы (available = true)</li>
     *   <li>Частичное совпадение (LIKE %text%)</li>
     * </ul>
     *
     * @apiNote <b>Примеры:</b>
     * <pre>
     * {@code
     * // Найдет "Аккумуляторная дрель" и "Мощная дрель"
     * List<Item> results = itemRepository.searchItemsByNameAndDescription("дрель");
     *
     * // Не найдет ничего, если text пустой
     * List<Item> emptyResults = itemRepository.searchItemsByNameAndDescription("");
     * }
     * </pre>
     *
     * @see ItemServiceImpl#searchItems(String)
     */
    @Query("SELECT i FROM Item i " +
            "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND i.available = true")
    List<Item> searchItemsByNameAndDescription(String text);
    /**
     * Находит все предметы, связанные с указанным запросом.
     * <p>
     * Используется для получения списка предметов, которые были созданы
     * в ответ на конкретный запрос. Возвращает полный список без пагинации.
     * </p>
     *
     * @param requestId идентификатор запроса, не должен быть null
     * @return список предметов, связанных с запросом
     *
     * @apiNote <b>Контекст использования:</b>
     * <ul>
     *   <li>Отображение предметов в ответе на запрос</li>
     *   <li>Получение статистики по запросам</li>
     *   <li>Управление связями между запросами и предметами</li>
     * </ul>
     *
     * @apiNote <b>Пример SQL запроса:</b>
     * <pre>
     * SELECT * FROM items WHERE request_id = :requestId
     * </pre>
     */
    List<Item> findAllByRequestId(Integer requestId);
}

