package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.ResponseItemConciseDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.mapper.CommentMapper;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
/**
 * Реализация сервиса для управления предметами (вещами) в системе шеринга.
 * <p>
 * Предоставляет бизнес-логику для операций CRUD с предметами, включая создание,
 * обновление, поиск, получение информации и управление комментариями.
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
 * @see ItemService
 * @see ItemRepository
 * @see ItemMapper
 * @since 2025
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    /**
     * Репозиторий для работы с данными предметов в базе данных.
     */
    private final ItemRepository itemRepository;

    /**
     * Репозиторий для работы с данными пользователей.
     */
    private final UserRepository userRepository;

    /**
     * Репозиторий для работы с данными бронирований.
     */
    private final BookingRepository bookingRepository;

    /**
     * Маппер для преобразования между сущностями бронирований и DTO.
     */
    private final BookingMapper bookingMapper;

    /**
     * Репозиторий для работы с комментариями.
     */
    private final CommentRepository commentRepository;

    /**
     * Маппер для преобразования между сущностями комментариев и DTO.
     */
    private final CommentMapper commentMapper;

    /**
     * Репозиторий для работы с запросами на предметы.
     */
    private final ItemRequestRepository requestRepository;

    /**
     * Создает новый предмет в системе.
     * <p>
     * Проверяет существование пользователя-владельца.
     * Проверяет, существует ли запрос.
     * Устанавливает владельца предмета и привязывает к запросу если указано.
     * </p>
     *
     * @param userId идентификатор пользователя-владельца
     * @param itemDto DTO с данными для создания предмета
     * @return ItemDto созданный предмет с присвоенным идентификатором
     * @throws NotFoundException если пользователь или запрос не найдены
     *
     * @see ItemService#create(Integer, ItemDto)
     */

    @Override
    @Transactional
    public ItemDto create(Integer userId, ItemDto itemDto) {
        log.debug("Получен запрос на добавление вещи пользователем {}, данные вещи {}", userId, itemDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден, при создании вещи!"));
        Item item = ItemMapper.mapToModel(itemDto);
        item.setOwner(user);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запроса с id " + itemDto.getRequestId() +
                            " не существует!"));
            item.setRequest(itemRequest);
        }
        Item savedItem = itemRepository.save(item);
        log.info("Вещь создана: {}", savedItem);
        return ItemMapper.mapToDto(savedItem);
    }
    /**
     * Обновляет данные существующего предмета.
     * <p>
     * Выполняет частичное обновление - изменяются только те поля, которые не равны null.
     * Проверяет права доступа - обновлять предмет может только владелец предмета.
     * </p>
     *
     * @param userId идентификатор пользователя, выполняющего обновление
     * @param itemId идентификатор предмета для обновления
     * @param updateItemDto DTO с данными для обновления
     * @return ItemDto обновленный предмет
     * @throws NotFoundException если предмет или пользователь не найдены
     * @throws BadRequestException если пользователь не является владельцем
     *
     * @see ItemService#update(Integer, Integer, UpdateItemDto)
     */
    @Override
    @Transactional
    public ItemDto update(Integer userId, Integer itemId, UpdateItemDto updateItemDto) {
        log.debug("Получен запрос на обновление вещи пользователем {}, данные вещи {}", userId, updateItemDto);
        Item item = checkAndGet(userId, itemId);
        ItemMapper.mapToModelFromUpdatedItem(updateItemDto, item);
        log.info("Вещь {}, обновлена", itemId);
        return ItemMapper.mapToDto(itemRepository.save(item));
    }
    /**
     * Возвращает предмет по идентификатору с расширенной информацией.
     * <p>
     * Для владельца предмета включает информацию о ближайших бронированиях.
     * Для всех пользователей включает список комментариев.
     * Информация о бронированиях доступна только владельцу предмета.
     * </p>
     *
     * @param userId идентификатор пользователя, запрашивающего информацию
     * @param itemId идентификатор запрашиваемого предмета
     * @return ItemWithBookingDto предмет с информацией о бронированиях и комментариями
     * @throws NotFoundException если предмет не найден
     *
     * @see ItemService#getById(Integer, Integer)
     */
    @Override
    @Transactional(readOnly = true)
    public ItemWithBookingDto getById(Integer userId, Integer itemId) {
        log.debug("Получен запрос на получение вещи по id = {},пользователем с id {}", itemId, userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена!"));
        log.debug("Получена вещь из базы данных: {}", item);
        Booking lastBooking = null;
        Booking nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
            lastBooking = bookingRepository.findByItemIdLastBooking(itemId, Status.APPROVED, LocalDateTime.now());
            nextBooking = bookingRepository.findByItemIdNextBooking(itemId, Status.APPROVED, LocalDateTime.now());
        }

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(commentMapper::mapToDto)
                .toList();
        log.debug("Получен список отзывов для вещи с id = {}", itemId);
        return ItemMapper.mapToItemWithBooking(item, bookingMapper.mapToDto(lastBooking),
                    bookingMapper.mapToDto(nextBooking), comments);
    }
    /**
     * Возвращает список предметов пользователя с пагинацией.
     * <p>
     * Предоставляет компактное представление предметов без детальной информации
     * о бронированиях.
     * Проверяет, существует ли пользователь по переданному идентификатору.
     * </p>
     *
     * @param userId идентификатор пользователя-владельца
     * @param from начальный элемент для пагинации (offset)
     * @param size количество элементов на странице (limit)
     * @return список ResponseItemConciseDto с компактной информацией о предметах
     * @throws NotFoundException если пользователь не найден
     * @throws BadRequestException при некорректных параметрах пагинации
     *
     * @see ItemService#getItemsForUser(Integer, Integer, Integer)
     */
    @Override
    @Transactional(readOnly = true)
    public List<ResponseItemConciseDto> getItemsForUser(Integer userId, Integer from, Integer size) {
        log.debug("Получен запрос на получение списка вещей пользователя с id = {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден!"));

        Pageable pageable = PageRequest.of(from / size, size);
        Page<Item> itemPage = itemRepository.findItemsByUserId(userId, pageable);

        return itemPage.getContent()
                .stream()
                .map(ItemMapper::mapToResponseConcise)
                .toList();
    }
    /**
     * Выполняет поиск предметов по названию и описанию.
     * <p>
     * Поиск осуществляется только среди доступных предметов (available = true).
     * Возвращает компактное представление результатов для оптимизации.
     * При пустом поисковом запросе возвращает пустой список.
     * </p>
     *
     * @param text текст для поиска
     * @return список ResponseItemConciseDto с результатами поиска
     *
     * @see ItemService#searchItems(String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<ResponseItemConciseDto> searchItems(String text) {
        log.debug("Получен запрос: поиск вещей по имени или описанию!");
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.searchItemsByNameAndDescription(text).stream()
                .map(ItemMapper::mapToResponseConcise)
                .toList();
    }
    /**
     * Создает комментарий к предмету.
     * <p>
     * Проверяет, что пользователь действительно брал предмет в аренду
     * и завершил бронирование. Комментарий может оставить только пользователь,
     * который ранее арендовал данный предмет.
     * </p>
     *
     * @param commentRequestDto DTO с данными комментария
     * @param itemId идентификатор предмета, к которому оставляется комментарий
     * @param userId идентификатор пользователя, оставляющего комментарий
     * @return CommentResponseDto созданный комментарий с информацией об авторе
     * @throws NotFoundException если предмет или пользователь не найдены
     * @throws BadRequestException если пользователь не арендовал предмет
     *
     * @see ItemService#createComment(CommentRequestDto, Integer, Integer)
     */
    @Override
    @Transactional
    public CommentResponseDto createComment(CommentRequestDto commentRequestDto, Integer itemId, Integer userId) {
        log.debug("Получен запрос на создания отзыва пользователем с id = {}, для вещи с id = {}", userId, itemId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден, при обновлении вещи!"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена!"));
        boolean hasComment = bookingRepository.existsApprovedPastBookingForItem(userId, itemId, Status.APPROVED, LocalDateTime.now());

        if (!hasComment) {
            log.error("Пользователь с id = {}, не брал вещь с id = {} в аренду", userId, itemId);
            throw new BadRequestException("Пользователь не брал эту вещь в аренду");
        }

        Comment comment = commentMapper.toEntity(commentRequestDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        log.info("Отзыв создан с id = {}", savedComment.getId());
        return new CommentResponseDto(savedComment.getId(), savedComment.getText(), savedComment.getItem().getId(),
                savedComment.getAuthor().getName(), savedComment.getCreated());
    }
    /**
     * Проверяет существование пользователя и предмета, а также права доступа.
     * <p>
     * Вспомогательный метод для проверки перед операциями обновления.
     * Убеждается, что пользователь существует и является владельцем предмета.
     * </p>
     *
     * @param userId идентификатор пользователя для проверки
     * @param itemId идентификатор предмета для проверки
     * @return Item предмет, если проверки пройдены успешно
     * @throws NotFoundException если пользователь или предмет не найдены
     * @throws BadRequestException если пользователь не является владельцем предмета
     */
    private Item checkAndGet(Integer userId, Integer itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден!"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена!"));
        if (!item.getOwner().equals(user)) {
            log.error("Вещь с id {}, добавлена другим пользователем!", itemId);
            throw new BadRequestException("Вещь с id " + itemId + " добавлена другим пользователем,!");
        }
        return item;
    }
}
