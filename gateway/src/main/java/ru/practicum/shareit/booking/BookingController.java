package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;



@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    private static final String USER_ID_IN_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getAllBookingsCurrentUser(@RequestHeader(USER_ID_IN_HEADER) @Positive Integer userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}", stateParam, userId);
        return bookingClient.getAllBookingsCurrentUser(userId, state);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader (USER_ID_IN_HEADER) @Positive Integer userId,
                                      @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.add(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader (USER_ID_IN_HEADER) @Positive Integer userId,
                                          @PathVariable @Positive Integer bookingId,
                                          @RequestParam Boolean approved) {
        return bookingClient.approve(userId, bookingId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsItemsUser(@RequestHeader(USER_ID_IN_HEADER) Integer userId,
                                                    @RequestParam(defaultValue = "ALL") String stateParam) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getAllBookingsItemsUser(userId, state);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@PathVariable @Positive Long bookingId,
                                             @RequestHeader(USER_ID_IN_HEADER) @Positive Integer userId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getById(userId, bookingId);
    }
}