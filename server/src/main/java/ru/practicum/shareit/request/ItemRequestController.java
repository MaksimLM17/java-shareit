package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private static final String USER_ID_IN_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestFullDto create(@RequestHeader(USER_ID_IN_HEADER)Integer userId,
                                     @RequestBody ItemRequestInDto itemRequestInDto) {
        return requestService.create(userId, itemRequestInDto);
    }

    @GetMapping
    public List<ItemRequestWithResponsesDto> getAllOwn(@RequestHeader(USER_ID_IN_HEADER)Integer userId) {
        return requestService.getAllOwn(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestFullDto> getAllOthers(@RequestHeader(USER_ID_IN_HEADER)Integer userId) {
        return requestService.getAllOthers(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithResponsesDto getById(@PathVariable Integer requestId) {
        return requestService.getById(requestId);
    }
}
