package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.CommentRequestDto;
import ru.practicum.shareit.comment.CommentResponseDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.ResponseItemConciseDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.mapper.CommentMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private final Integer userId = 1;
    private final Integer itemId = 1;
    private final Integer requestId = 1;
    private final User user = new User(userId, "Антон", "antony@example.com");
    private final Item item = new Item(itemId, "Сабельная пила", "500 рублей/сутки",
            true, user, null);
    private final ItemDto itemDto = new ItemDto(itemId, "Сабельная пила", "500 рублей/сутки",
            true, user, null);
    private final UpdateItemDto updateItemDto = new UpdateItemDto();
    private final ItemWithBookingDto itemWithBookingDto = new ItemWithBookingDto();
    private final ResponseItemConciseDto conciseDto = new ResponseItemConciseDto("Сабельная пила",
            "500 рублей/сутки");
    private final CommentResponseDto commentResponseDto = new CommentResponseDto(1, "Замечательный инструмент!", itemId,
            "Марк", LocalDateTime.now());

    @Test
    void create_shouldSaveNewItem() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.create(userId, itemDto);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
        verify(userRepository).findById(userId);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void create_shouldSaveItemWithRequest() {
        itemDto.setRequestId(requestId);
        ItemRequest request = new ItemRequest();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.create(userId, itemDto);

        assertNotNull(result);
        verify(requestRepository).findById(requestId);
    }

    @Test
    void create_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(userId, itemDto));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void update_shouldUpdateItem() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        updateItemDto.setName("Updated Drill");
        ItemDto result = itemService.update(userId, itemId, updateItemDto);

        assertNotNull(result);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void update_shouldThrowExceptionWhenNotOwner() {
        User otherUser = new User(2, "Other", "other@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(otherUser));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () -> itemService.update(userId, itemId, updateItemDto));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void getById_shouldReturnItemWithBookingsForOwner() {
        item.setOwner(user);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdLastBooking(eq(itemId), any(), any())).thenReturn(null);
        when(bookingRepository.findByItemIdNextBooking(eq(itemId), any(), any())).thenReturn(null);
        when(commentRepository.findByItemId(itemId)).thenReturn(List.of());

        ItemWithBookingDto result = itemService.getById(userId, itemId);

        assertNotNull(result);
        verify(bookingRepository).findByItemIdLastBooking(anyInt(), any(), any());
        verify(bookingRepository).findByItemIdNextBooking(anyInt(), any(), any());
        verify(commentRepository).findByItemId(itemId);
    }

    @Test
    void getById_shouldReturnItemWithoutBookingsForNonOwner() {
        Integer nonOwnerId = 2;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(itemId)).thenReturn(List.of());

        ItemWithBookingDto result = itemService.getById(nonOwnerId, itemId);

        assertNotNull(result);
        verify(bookingRepository, never()).findByItemIdLastBooking(anyInt(), any(), any());
        verify(bookingRepository, never()).findByItemIdNextBooking(anyInt(), any(), any());
    }

    @Test
    void getItemsForUser_shouldReturnPaginatedItems() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> page = new PageImpl<>(List.of(item), pageable, 1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findItemsByUserId(userId, pageable)).thenReturn(page);

        List<ResponseItemConciseDto> result = itemService.getItemsForUser(userId, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(itemRepository).findItemsByUserId(userId, pageable);
    }

    @Test
    void searchItems_shouldReturnEmptyListForBlankText() {
        List<ResponseItemConciseDto> result = itemService.searchItems(" ");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemRepository, never()).searchItemsByNameAndDescription(any());
    }

    @Test
    void searchItems_shouldReturnMatchingItems() {
        when(itemRepository.searchItemsByNameAndDescription("пила")).thenReturn(List.of(item));

        List<ResponseItemConciseDto> result = itemService.searchItems("пила");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(itemRepository).searchItemsByNameAndDescription("пила");
    }

    @Test
    void createComment_shouldSaveComment() {
        CommentRequestDto requestDto = new CommentRequestDto("Замечательный инструмент!");
        LocalDateTime fixedTime = LocalDateTime.of(2023, 1, 1, 12, 0); // Фиксированное время


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsApprovedPastBookingForItem(
                eq(userId),
                eq(itemId),
                eq(Status.APPROVED),
                any(LocalDateTime.class)
        )).thenReturn(true);

        Comment savedComment = new Comment(1, "Замечательный инструмент!", item, user, fixedTime);
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentResponseDto result = itemService.createComment(requestDto, itemId, userId);

        assertNotNull(result);
        assertEquals("Замечательный инструмент!", result.getText());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void createComment_shouldThrowExceptionWhenNoBooking() {
        CommentRequestDto requestDto = new CommentRequestDto("Замечательный инструмент!");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsApprovedPastBookingForItem(
                eq(userId),
                eq(itemId),
                eq(Status.APPROVED),
                any(LocalDateTime.class)
        )).thenReturn(false);

        assertThrows(BadRequestException.class, () ->
                itemService.createComment(requestDto, itemId, userId));

        verify(commentRepository, never()).save(any());
    }
}
