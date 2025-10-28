package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.CommentRequestDto;
import ru.practicum.shareit.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.ResponseItemConciseDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;
/**
 * Сервис для управления предметами (вещами) в системе шеринга.
 * <p>
 * Предоставляет бизнес-логику для операций с предметами: создание, обновление,
 * поиск, получение информации и управление комментариями.
 * Обеспечивает проверку прав доступа, валидацию данных и обработку бизнес-правил.
 * </p>
 *
 * <p><b>Основные функции:</b></p>
 * <ul>
 *   <li>Управление жизненным циклом предметов</li>
 *   <li>Поиск и фильтрация предметов</li>
 *   <li>Управление бронированиями и доступностью</li>
 *   <li>Обработка комментариев и отзывов</li>
 *   <li>Проверка прав доступа и владения</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see ItemServiceImpl
 * @see ItemController
 * @see ItemRepository
 * @since 2025
 */
public interface ItemService {
    /**
     * Создает новый предмет в системе.
     * <p>
     * Выполняет проверку существования пользователя-владельца и опционального запроса.
     * Устанавливает владельца предмета и привязывает к запросу если указано.
     * </p>
     *
     * @param userId идентификатор пользователя-владельца, не должен быть null
     * @param itemDto DTO с данными для создания предмета, не должен быть null
     * @return ItemDto созданный предмет с присвоенным идентификатором
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь или запрос не найдены
     * @throws ru.practicum.shareit.exception.BadRequestException при нарушении бизнес-правил
     *
     * @see ItemDto
     */
    ItemDto create(Integer userId, ItemDto itemDto);
    /**
     * Обновляет данные существующего предмета.
     * <p>
     * Выполняет частичное обновление - изменяются только те поля, которые не равны null.
     * Проверяет права доступа - обновлять предмет может только его владелец.
     * </p>
     *
     * @param userId идентификатор пользователя, выполняющего обновление
     * @param itemId идентификатор предмета для обновления
     * @param updateItemDto DTO с данными для обновления, не должен быть null
     * @return ItemDto обновленный предмет
     * @throws ru.practicum.shareit.exception.NotFoundException если предмет или пользователь не найдены
     * @throws ru.practicum.shareit.exception.BadRequestException если пользователь не является владельцем
     *
     * @see UpdateItemDto
     */
    ItemDto update(Integer userId,Integer itemId, UpdateItemDto updateItemDto);
    /**
     * Возвращает предмет по идентификатору с расширенной информацией.
     * <p>
     * Для владельца предмета включает информацию о ближайших бронированиях.
     * Для всех пользователей включает список комментариев.
     * </p>
     *
     * @param userId идентификатор пользователя, запрашивающего информацию
     * @param itemId идентификатор запрашиваемого предмета
     * @return ItemWithBookingDto предмет с информацией о бронированиях и комментариями
     * @throws ru.practicum.shareit.exception.NotFoundException если предмет не найден
     *
     * @see ItemWithBookingDto
     */
    ItemWithBookingDto getById(Integer userId, Integer itemId);
    /**
     * Возвращает список предметов пользователя с пагинацией.
     * <p>
     * Предоставляет компактное представление предметов без детальной информации
     * о бронированиях. Используется для отображения в личном кабинете владельца.
     * </p>
     *
     * @param userId идентификатор пользователя-владельца
     * @param from начальный элемент для пагинации (offset)
     * @param size количество элементов на странице (limit)
     * @return список ResponseItemConciseDto с компактной информацией о предметах
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь не найден
     * @throws ru.practicum.shareit.exception.BadRequestException при некорректных параметрах пагинации
     *
     * @see ResponseItemConciseDto
     */
    List<ResponseItemConciseDto> getItemsForUser(Integer userId, Integer from, Integer size);
    /**
     * Выполняет поиск предметов по названию и описанию.
     * <p>
     * Поиск осуществляется только среди доступных предметов (available = true).
     * Возвращает компактное представление результатов для оптимизации.
     * При пустом поисковом запросе возвращает пустой список.
     * </p>
     *
     * @param text текст для поиска, не должен быть null
     * @return список ResponseItemConciseDto с результатами поиска
     *
     * @see ResponseItemConciseDto
     */
    List<ResponseItemConciseDto> searchItems(String text);
    /**
     * Создает комментарий к предмету.
     * <p>
     * Проверяет, что пользователь действительно брал предмет в аренду
     * и завершил бронирование. Комментарий может оставить только пользователь,
     * который ранее арендовал данный предмет.
     * </p>
     *
     * @param commentRequestDto DTO с данными комментария, не должен быть null
     * @param itemId идентификатор предмета, к которому оставляется комментарий
     * @param userId идентификатор пользователя, оставляющего комментарий
     * @return CommentResponseDto созданный комментарий с информацией об авторе
     * @throws ru.practicum.shareit.exception.NotFoundException если предмет или пользователь не найдены
     * @throws ru.practicum.shareit.exception.BadRequestException если пользователь не арендовал предмет
     *
     * @see CommentRequestDto
     * @see CommentResponseDto
     */
    CommentResponseDto createComment(CommentRequestDto commentRequestDto, Integer itemId, Integer userId);

}
