package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemResponseInRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
/**
 * Реализация сервиса для управления запросами на предметы.
 * <p>
 * Обеспечивает бизнес-логику работы с запросами: создание, получение собственных и чужих запросов,
 * поиск по идентификатору. Включает обработку связанных сущностей (пользователи, предметы-ответы).
 * </p>
 *
 * <p><b>Основные функции:</b></p>
 * <ul>
 *   <li>Создание новых запросов с валидацией пользователя</li>
 *   <li>Получение собственных запросов с ответами в виде предметов</li>
 *   <li>Получение запросов других пользователей для предложения помощи</li>
 *   <li>Поиск конкретного запроса по ID с полной информацией об ответах</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see ItemRequestService
 * @see ItemRequestRepository
 * @see UserRepository
 * @see ItemRepository
 * @see ItemRequestMapper
 * @since 2025
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    /**
     * Репозиторий для работы с пользователями.
     * Используется для проверки существования пользователя перед операциями.
     */
    private final UserRepository userRepository;

    /**
     * Репозиторий для работы с запросами на предметы.
     * Обеспечивает базовые CRUD операции и специализированные запросы.
     */
    private final ItemRequestRepository requestRepository;

    /**
     * Репозиторий для работы с предметами.
     * Используется для поиска предметов-ответов на запросы.
     */
    private final ItemRepository itemRepository;

    /**
     * Маппер для преобразования между сущностями и DTO.
     * Обеспечивает конвертацию данных между слоями приложения.
     */
    private final ItemRequestMapper requestMapper;

    /**
     * Создает новый запрос на предмет от имени указанного пользователя.
     * <p>
     * Процесс создания:
     * <ol>
     *   <li>Проверяет существование пользователя</li>
     *   <li>Создает новую сущность запроса из DTO</li>
     *   <li>Устанавливает связь с пользователем-создателем</li>
     *   <li>Сохраняет запрос в базу данных</li>
     *   <li>Возвращает DTO с присвоенным идентификатором</li>
     * </ol>
     * </p>
     *
     * @param userId идентификатор пользователя, создающего запрос
     * @param itemRequestInDto DTO с данными для создания запроса
     * @return ItemRequestFullDto созданный запрос с системными полями
     * @throws NotFoundException если пользователь с указанным ID не найден
     *
     * @apiNote Время создания запроса устанавливается автоматически в конструкторе ItemRequest
     */
    @Override
    public ItemRequestFullDto create(Integer userId, ItemRequestInDto itemRequestInDto) {
        log.debug("Получен запрос на создание реквеста пользователем {}, данные запроса {}", userId, itemRequestInDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден, при создании запроса!"));

        ItemRequest itemRequest = new ItemRequest(itemRequestInDto.getDescription());
        itemRequest.setRequester(user);
        ItemRequest savedItemRequest = requestRepository.save(itemRequest);
        log.info("Запрос создан с данными: {}", savedItemRequest);
        return requestMapper.mapToDto(savedItemRequest);
    }
    /**
     * Возвращает все запросы текущего пользователя вместе с ответами в виде предметов.
     * <p>
     * Для каждого запроса пользователя находит все предметы, которые были созданы
     * как ответ на этот запрос (имеют ссылку на request_id).
     * </p>
     *
     * @param userId идентификатор пользователя, чьи запросы требуется получить
     * @return List<ItemRequestWithResponsesDto> список запросов с ответами-предметами
     * @throws NotFoundException если пользователь с указанным ID не найден
     *
     * @apiNote Запросы сортируются по дате создания в порядке убывания (от новых к старым)
     */
    @Override
    public List<ItemRequestWithResponsesDto> getAllOwn(Integer userId) {
        log.debug("Получен запрос на получение списка реквестов с ответами, пользователем {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId +
                        " не найден, при получении своих запросов с ответами вещи!"));
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        log.info("Отправлен список размером: {}", requests.size());
        return requests.stream()
                .map(this::mapToResponses)
                .toList();
    }
    /**
     * Возвращает все запросы, созданные другими пользователями.
     * <p>
     * Используется для просмотра запросов, на которые текущий пользователь может предложить
     * свои предметы. Исключает из результатов запросы самого пользователя.
     * </p>
     *
     * @param userId идентификатор пользователя, который исключается из результатов
     * @return List<ItemRequestFullDto> список запросов других пользователей
     * @throws NotFoundException если пользователь с указанным ID не найден
     *
     * @apiNote Возвращает только базовую информацию без ответов для оптимизации
     */
    @Override
    public List<ItemRequestFullDto> getAllOthers(Integer userId) {
        log.debug("Получен запрос на получение списка реквестов созданных другими людьми, пользователем {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId +
                        " не найден, при получении запросов других пользователей!"));
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId);
        log.info("Отправлен список размером: {}", requests.size());
        return requests.stream()
                .map(requestMapper::mapToDto)
                .toList();
    }
    /**
     * Возвращает запрос по идентификатору с полной информацией об ответах.
     * <p>
     * Находит запрос по ID и собирает все предметы, которые ссылаются на этот запрос
     * как на причину создания (ответы других пользователей).
     * </p>
     *
     * @param requestId идентификатор запроса для поиска
     * @return ItemRequestWithResponsesDto запрос с полной информацией об ответах
     * @throws NotFoundException если запрос с указанным ID не найден
     *
     * @apiNote Доступно для всех аутентифицированных пользователей
     */
    @Override
    public ItemRequestWithResponsesDto getById(Integer requestId) {
        log.debug("Получен запрос на получение  реквеста по id: {}", requestId);
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId +
                        " не найден!"));
        return mapToResponses(itemRequest);
    }
    /**
     * Преобразует сущность запроса в DTO с ответами.
     * <p>
     * Вспомогательный метод, который для указанного запроса:
     * <ol>
     *   <li>Находит все предметы-ответы через ItemRepository</li>
     *   <li>Преобразует каждый предмет в DTO через ItemMapper</li>
     *   <li>Создает итоговый DTO с собранными ответами</li>
     * </ol>
     * </p>
     *
     * @param itemRequest сущность запроса для преобразования
     * @return ItemRequestWithResponsesDto DTO с ответами-предметами
     *
     * @see ItemMapper#mapToResponseForItemRequest(Item)
     */
    private ItemRequestWithResponsesDto mapToResponses(ItemRequest itemRequest) {
        List<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
        log.info("Получен список ответов на запрос: {}", items);
        List<ItemResponseInRequestDto> responses = items.stream()
                .map(ItemMapper::mapToResponseForItemRequest)
                .toList();

        return new ItemRequestWithResponsesDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                responses
        );
    }
}
