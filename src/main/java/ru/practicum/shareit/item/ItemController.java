package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemConciseDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.Collection;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final static String USER_ID_IN_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID_IN_HEADER) Integer userId, @RequestBody @Valid ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID_IN_HEADER) Integer userId,
                          @PathVariable Integer itemId, @RequestBody @Valid UpdateItemDto updateItemDto) {
        return itemService.update(userId, itemId, updateItemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable Integer itemId) {
        return itemService.getById(itemId);
    }

    @GetMapping
    public Collection<ResponseItemConciseDto> getItemsForUser(@RequestHeader(USER_ID_IN_HEADER) Integer userId) {
        return itemService.getItemsForUser(userId);
    }

    @GetMapping("/search")
    public Collection<ResponseItemConciseDto> searchItems(@RequestParam (required = false) String text) {
        return itemService.searchItems(text);
    }
}
