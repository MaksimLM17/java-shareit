package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private static final String USER_ID_IN_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto add(@RequestBody @Valid BookingRequestDto bookingRequestDto, @RequestHeader(USER_ID_IN_HEADER) Integer userId) {
        return bookingService.add(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable Integer bookingId, @RequestHeader(USER_ID_IN_HEADER) Integer userId,
                              @RequestParam Boolean approved) {
        return bookingService.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable Integer bookingId, @RequestHeader(USER_ID_IN_HEADER) Integer userId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsCurrentUser(@RequestHeader(USER_ID_IN_HEADER) Integer userId,
                                                     @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsCurrentUser(userId, state.toUpperCase());
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsItemsUser(@RequestHeader(USER_ID_IN_HEADER) Integer userId,
                                                    @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsItemsUser(userId, state.toUpperCase());
    }
}
