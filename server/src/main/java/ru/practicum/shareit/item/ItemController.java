package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentRequestDto;
import ru.practicum.shareit.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.ResponseItemConciseDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.Collection;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private static final String USER_ID_IN_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID_IN_HEADER) Integer userId, @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID_IN_HEADER) Integer userId,
                          @PathVariable Integer itemId, @RequestBody UpdateItemDto updateItemDto) {
        return itemService.update(userId, itemId, updateItemDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingDto getById(@RequestHeader(USER_ID_IN_HEADER) Integer userId, @PathVariable Integer itemId) {
        return itemService.getById(userId,itemId);
    }

    @GetMapping
    public Collection<ResponseItemConciseDto> getItemsForUser(@RequestHeader(USER_ID_IN_HEADER) Integer userId,
                                                              @RequestParam(defaultValue = "0") Integer from,
                                                              @RequestParam(defaultValue = "10") Integer size) {
        return itemService.getItemsForUser(userId, from,size);
    }

    @GetMapping("/search")
    public Collection<ResponseItemConciseDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(@RequestBody CommentRequestDto commentRequestDto, @PathVariable Integer itemId,
                                            @RequestHeader(USER_ID_IN_HEADER) Integer userId) {
        return itemService.createComment(commentRequestDto, itemId, userId);
    }
}
