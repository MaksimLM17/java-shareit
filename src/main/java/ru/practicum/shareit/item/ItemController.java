package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemConcise;
import ru.practicum.shareit.item.dto.UpdateItem;

import java.util.Collection;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody @Valid ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Integer userId,
                          @PathVariable Integer itemId, @RequestBody @Valid UpdateItem updateItem) {
        return itemService.update(userId, itemId, updateItem);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable Integer itemId) {
        return itemService.getById(itemId);
    }

    @GetMapping
    public Collection<ResponseItemConcise> getItemsForUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getItemsForUser(userId);
    }

    @GetMapping("/search")
    public Collection<ResponseItemConcise> searchItems(@RequestParam (required = false) String text) {
        return itemService.searchItems(text);
    }
}
