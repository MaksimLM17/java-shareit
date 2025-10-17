package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;
    private static final String USER_ID_IN_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_IN_HEADER) @Positive Integer userId,
                                         @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return requestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllOwn(@RequestHeader(USER_ID_IN_HEADER) @Positive Integer userId) {
        return requestClient.getAllOwn(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllOthers(@RequestHeader(USER_ID_IN_HEADER) @Positive Integer userId) {
        return requestClient.getAllOthers(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable @Positive Integer requestId) {
        return requestClient.getById(requestId);
    }
}
