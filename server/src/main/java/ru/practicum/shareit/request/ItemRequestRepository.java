package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Репозиторий для работы с сущностями запросов на предметы (ItemRequest).
 * <p>
 * Обеспечивает доступ к данным запросов в базе данных с использованием Spring Data JPA.
 * Расширяет стандартный интерфейс JpaRepository, предоставляя базовые CRUD операции,
 * и добавляет специализированные методы для поиска запросов по критериям.
 * </p>
 *
 * <p><b>Основные возможности:</b></p>
 * <ul>
 *   <li>Стандартные CRUD операции (наследуются от JpaRepository)</li>
 *   <li>Поиск запросов конкретного пользователя с сортировкой</li>
 *   <li>Поиск запросов других пользователей с сортировкой</li>
 *   <li>Автоматическая генерация SQL запросов на основе имен методов</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @see ItemRequest
 * @see ItemRequestService
 * @see ItemRequestServiceImpl
 * @since 2025
 */
@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    /**
     * Находит все запросы, созданные указанным пользователем, с сортировкой по дате создания (по убыванию).
     * <p>
     * Генерирует SQL запрос вида:
     * <pre>{@code
     * SELECT * FROM item_requests
     * WHERE requester_id = ?
     * ORDER BY created DESC
     * }</pre>
     * </p>
     *
     * @param requesterId идентификатор пользователя-создателя запросов
     * @return List<ItemRequest> список запросов пользователя, отсортированный от новых к старым
     *
     * @apiNote Используется для получения собственных запросов пользователя
     * @see ItemRequestService#getAllOwn(Integer)
     */
    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Integer requesterId);
    /**
     * Находит все запросы, созданные пользователями, кроме указанного, с сортировкой по дате создания (по убыванию).
     * <p>
     * Генерирует SQL запрос вида:
     * <pre>{@code
     * SELECT * FROM item_requests
     * WHERE requester_id != ?
     * ORDER BY created DESC
     * }</pre>
     * </p>
     *
     * @param requesterId идентификатор пользователя, который исключается из результатов
     * @return List<ItemRequest> список запросов других пользователей, отсортированный от новых к старым
     *
     * @apiNote Используется для поиска запросов, на которые можно предложить свои предметы
     * @see ItemRequestService#getAllOthers(Integer)
     */
    List<ItemRequest> findAllByRequesterIdNotOrderByCreatedDesc(Integer requesterId);

}

