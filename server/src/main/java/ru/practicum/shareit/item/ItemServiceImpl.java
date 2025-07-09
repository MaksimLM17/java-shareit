package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;


    @Override
    public ItemDto create(Integer userId, ItemDto itemDto) {
        log.debug("Получен запрос на добавление вещи пользователем {}, данные вещи {}", userId, itemDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден, при создании вещи!"));
        itemDto.setOwner(user);
        Item item = itemRepository.save(ItemMapper.mapToModel(itemDto));
        log.info("Вещь создана: {}", item);
        return ItemMapper.mapToDto(item);
    }

    @Override
    public ItemDto update(Integer userId, Integer itemId, UpdateItemDto updateItemDto) {
        log.debug("Получен запрос на обновление вещи пользователем {}, данные вещи {}", userId, updateItemDto);
        Item item = checkAndGet(userId, itemId);
        ItemMapper.mapToModelFromUpdatedItem(updateItemDto, item);
        log.info("Вещь {}, обновлена", itemId);
        return ItemMapper.mapToDto(itemRepository.save(item));
    }

    @Override
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

    @Override
    public List<ResponseItemConciseDto> getItemsForUser(Integer userId) {
        log.debug("Получен запрос на получение списка вещей пользователя с id = {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден!"));
        return itemRepository.findItemsByUserId(userId).stream()
                .map(ItemMapper::mapToResponseConcise)
                .toList();
    }

    @Override
    public List<ResponseItemConciseDto> searchItems(String text) {
        log.debug("Получен запрос: поиск вещей по имени или описанию!");
        if (text.isBlank() || text.isEmpty()) {
            return List.of();
        }
        return itemRepository.searchItemsByNameAndDescription(text).stream()
                .map(ItemMapper::mapToResponseConcise)
                .toList();
    }

    @Override
    public CommentResponseDto createComment(CommentRequestDto commentRequestDto, Integer itemId, Integer userId) {
        log.debug("Получен запрос на создания отзыва пользователем с id = {}, для вещи с id = {}", userId, itemId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден, при обновлении вещи!"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена!"));
        boolean hasComment = bookingRepository.existsApprovedPastBookingForItem(userId, itemId, Status.APPROVED);

        if (!hasComment) {
            log.error("Пользователь с id = {}, не брал вещь с id = {} в аренду", userId, itemId);
            throw new BadRequestException("Пользователь не брал эту вещь в аренду");
        }
        Comment comment = createCommentFromRequest(commentRequestDto, item, user);
        Comment savedComment = commentRepository.save(comment);
        log.info("Отзыв создан с id = {}", savedComment.getId());
        return new CommentResponseDto(savedComment.getId(), savedComment.getText(), savedComment.getItem().getId(),
                savedComment.getAuthor().getName(), savedComment.getCreated());
    }

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

    private Comment createCommentFromRequest(CommentRequestDto commentRequestDto,Item item, User user) {
        return new Comment(commentRequestDto.getText(),
                item, user);
    }
}
