package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;
    private static final String USER_ID_IN_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_IN_HEADER) @Positive Integer userId,
                                         @RequestBody @Valid ItemDto itemDto) {
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(USER_ID_IN_HEADER) @Positive Integer userId,
                                         @PathVariable @Positive Integer itemId,
                                         @RequestBody @Valid UpdateItemDto updateItemDto) {
        return itemClient.update(userId, itemId, updateItemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_IN_HEADER) @Positive Integer userId,
                                          @PathVariable @Positive Integer itemId) {
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsForUser(@RequestHeader(USER_ID_IN_HEADER) @Positive Integer userId) {
        return itemClient.getItemsForUser(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam (required = false) String text) {
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_ID_IN_HEADER) @Positive Integer userId,
                                                @PathVariable @Positive Integer itemId,
                                                @RequestBody @Valid CommentRequestDto commentRequestDto) {
        return itemClient.createComment(userId, itemId, commentRequestDto);
    }

}
