package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {

    BookingDto add(BookingRequestDto bookingRequestDto, Integer userId);

    BookingDto approve(Integer bookingId, Integer userId, boolean approved);

    BookingDto getById(Integer bookingId, Integer userId);

    List<BookingDto> getAllBookingsCurrentUser(Integer userId, String state);

    List<BookingDto> getAllBookingsItemsUser(Integer userId, String state);
}
