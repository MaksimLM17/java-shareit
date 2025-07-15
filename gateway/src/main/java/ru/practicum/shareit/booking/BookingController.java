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

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader (USER_ID_IN_HEADER) @Positive Integer userId,
                                      @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Получен запрос на создание бронирования с данными: {}, userId={}", requestDto, userId);
        return bookingClient.add(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader (USER_ID_IN_HEADER) @Positive Integer userId,
                                          @PathVariable @Positive Integer bookingId,
                                          @RequestParam Boolean approved) {
        log.info("Получен запроса на обновление статуса брони с данными: userId = {}, bookingId = {}, approved = {}",
                userId, bookingId, approved);
        return bookingClient.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@PathVariable @Positive Long bookingId,
                                             @RequestHeader(USER_ID_IN_HEADER) @Positive Integer userId) {
        log.info("Получен запрос на получение бронирований по id = {}, userId={}", bookingId, userId);
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsCurrentUser(@RequestHeader(USER_ID_IN_HEADER) @Positive Integer userId,
                                                            @RequestParam(name = "state", defaultValue = "all") String stateParam) {
        log.info("Получен запрос на получение всех бронирований пользователя с данными: userId = {}, stateParam = {}",
                userId, stateParam);
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный статус: " + stateParam));
        log.info("Get booking with state {}, userId={}", stateParam, userId);
        return bookingClient.getAllBookingsCurrentUser(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsItemsUser(@RequestHeader(USER_ID_IN_HEADER) Integer userId,
                                                          @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {
        log.info("Получен запрос на получение бронирований всех вещей пользователя с данными: userId = {}, stateParam = {}",
                userId, stateParam);
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный статус: " + stateParam));
        return bookingClient.getAllBookingsItemsUser(userId, state);
    }
}