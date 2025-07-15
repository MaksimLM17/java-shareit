package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private final Integer userId = 1;
    private final Integer itemId = 1;
    private final Integer bookingId = 1;
    private final User user = new User(userId, "Артем", "tema123@example.com");
    private final User owner = new User(2, "Виталий", "vintik@example.com");
    private final Item item = new Item(itemId, "Триммер", "Бензиновый, полный бак", true, owner, null);
    private final Booking booking = new Booking();
    private final BookingDto bookingDto = new BookingDto();
    private final BookingRequestDto bookingRequestDto = new BookingRequestDto(itemId,
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusDays(1));

    @Test
    void add_shouldCreateBooking() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingMapper.mapToModelFromRequest(any())).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.mapToDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.add(bookingRequestDto, userId);

        assertNotNull(result);
        verify(bookingRepository).save(booking);
    }

    @Test
    void add_shouldThrowWhenBookingOwnItem() {
        item.setOwner(user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () ->
                bookingService.add(bookingRequestDto, userId));
    }

    @Test
    void add_shouldThrowWhenItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () ->
                bookingService.add(bookingRequestDto, userId));
    }

    @Test
    void add_shouldThrowWhenInvalidDates() {
        BookingRequestDto invalidRequest = new BookingRequestDto(itemId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () ->
                bookingService.add(invalidRequest, userId));
    }

    @Test
    void approve_shouldApproveBooking() {
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.mapToDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.approve(bookingId, owner.getId(), true);

        assertNotNull(result);
        assertEquals(Status.APPROVED, booking.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void approve_shouldRejectBooking() {
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.mapToDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.approve(bookingId, owner.getId(), false);

        assertNotNull(result);
        assertEquals(Status.REJECTED, booking.getStatus());
    }

    @Test
    void approve_shouldThrowWhenNotOwner() {
        booking.setItem(item);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class, () ->
                bookingService.approve(bookingId, userId, true));
    }

    @Test
    void getById_shouldReturnBookingForOwner() {
        booking.setItem(item);
        booking.setBooker(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingMapper.mapToDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.getById(bookingId, owner.getId());

        assertNotNull(result);
    }

    @Test
    void getById_shouldReturnBookingForBooker() {
        booking.setItem(item);
        booking.setBooker(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingMapper.mapToDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.getById(bookingId, userId);

        assertNotNull(result);
    }

    @Test
    void getById_shouldThrowWhenNotOwnerOrBooker() {
        booking.setItem(item);
        booking.setBooker(new User(3, "Other", "other@example.com"));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () ->
                bookingService.getById(bookingId, userId));
    }

    @Test
    void getAllBookingsCurrentUser_shouldReturnBookings() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.getAllBookingsByUserId(userId, "ALL")).thenReturn(List.of(booking));
        when(bookingMapper.mapToDto(booking)).thenReturn(bookingDto);

        List<BookingDto> result = bookingService.getAllBookingsCurrentUser(userId, "ALL");

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getAllBookingsItemsUser_shouldReturnBookings() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.getAllBookingsItemsUser(owner.getId(), "ALL")).thenReturn(List.of(booking));
        when(bookingMapper.mapToDto(booking)).thenReturn(bookingDto);

        List<BookingDto> result = bookingService.getAllBookingsItemsUser(owner.getId(), "ALL");

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
