package ru.practicum.shareit.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemConcise;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDto mapToDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.isAvailable());
    }

    public static Item mapToModel(ItemDto itemDto) {
        return new Item(itemDto.getName(), itemDto.getDescription(),itemDto.getAvailable());
    }

    public static ResponseItemConcise mapToResponseConcise(Item item) {
        return new ResponseItemConcise(item.getName(), item.getDescription());
    }

}
